package com.iecube.community.model.Exam_timing.Service;

import com.iecube.community.model.Exam.vo.StuExamInfoVo;
import com.iecube.community.model.Exam_timing.Dto.ExamFinishMessage;
import com.iecube.community.model.Exam_timing.Dto.TimingTask;
import com.iecube.community.model.Exam_timing.Mapper.TimingTaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 计时任务服务（适配StuExamInfoVo + MySQL + MyBatis + RabbitMQ）
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TimingTaskService implements ApplicationListener<ContextRefreshedEvent> {

    private final TimingTaskMapper timingTaskMapper;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Value("${timing.task.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${timing.task.rabbitmq.exchange}")
    private String exchangeName;

    /**
     * 维护运行中的任务（esId -> 异步任务Future）
     */
    private final Map<Long, Future<?>> runningTasks = new ConcurrentHashMap<>();

    /**
     * 启动考试计时任务
     * @param stuExamInfoVo 考试信息VO
     */
    @Transactional
    public void startExamTask(StuExamInfoVo stuExamInfoVo) {
        Long esId = stuExamInfoVo.getEsId();
        // 1. 检查是否已有运行中的同名任务
        TimingTask existingTask = timingTaskMapper.selectByEsIdAndStatus(esId, TimingTask.TaskStatus.RUNNING);
        if (existingTask != null) {
            log.warn("esId: {} 已有运行中的考试计时任务，无需重复创建", esId);
            return;
        }

        // 2. 构建并保存任务实体
        TimingTask task = new TimingTask();
        task.setEsId(esId);
        task.setExamId(stuExamInfoVo.getExamId());
        task.setProjectId(stuExamInfoVo.getProjectId());
        task.setStudentId(stuExamInfoVo.getStudentId());
        task.setStartTime(stuExamInfoVo.getStartTime()); // 开始时间
        task.setDurationMinutes(stuExamInfoVo.getDuration()); // 持续时长（分钟）
        task.setStatus(TimingTask.TaskStatus.RUNNING);
        timingTaskMapper.insert(task);

        // 3. 提交异步计时任务
        Future<?> future = taskExecutor.submit(() -> executeExamTimingTask(stuExamInfoVo));
        runningTasks.put(esId, future);
        log.info("考试计时任务启动成功，esId: {}, 学生ID: {}, 考试ID: {}, 开始时间: {}, 持续时长: {}分钟",
                esId, stuExamInfoVo.getStudentId(), stuExamInfoVo.getExamId(),
                stuExamInfoVo.getStartTime(), stuExamInfoVo.getDuration());
    }

    /**
     * 手动中断考试计时任务
     * @param esId 任务ID
     */
    @Transactional
    public void interruptExamTask(Long esId) {
        // 1. 获取运行中的任务
        Future<?> future = runningTasks.get(esId);
        TimingTask task = timingTaskMapper.selectByEsIdAndStatus(esId, TimingTask.TaskStatus.RUNNING);

        if (task == null) {
            log.warn("esId: {} 无运行中的考试计时任务，无需中断", esId);
            return;
        }

        // 2. 中断异步任务
        if (future != null && !future.isDone()) {
            future.cancel(true);
            runningTasks.remove(esId);
        }

        // 3. 更新任务状态
        timingTaskMapper.updateStatus(esId, TimingTask.TaskStatus.INTERRUPTED);

        // 4. 发送手动中断消息
        sendExamFinishMessage(esId, task.getExamId(), task.getStudentId(), "MANUAL");
        log.info("考试计时任务已手动中断，esId: {}", esId);
    }

    /**
     * 核心考试计时逻辑
     */
    private void executeExamTimingTask(StuExamInfoVo stuExamInfoVo) {
        Long esId = stuExamInfoVo.getEsId();
        Date startTime = stuExamInfoVo.getStartTime();
        Integer durationMinutes = stuExamInfoVo.getDuration();
        // 转换为结束时间（开始时间 + 持续分钟数）
        Date endTime = DateUtils.addMinutes(startTime, durationMinutes);

        try {
            while (true) {
                // 检查是否被中断
                if (Thread.currentThread().isInterrupted()) {
                    log.info("考试计时任务被中断，esId: {}", esId);
                    return;
                }

                Date now = new Date();
                // 判断是否到达结束时间
                if (now.after(endTime)) {
                    // 发送自动结束消息
                    sendExamFinishMessage(esId, stuExamInfoVo.getExamId(), stuExamInfoVo.getStudentId(), "AUTO");

                    // 更新任务状态
                    timingTaskMapper.updateStatus(esId, TimingTask.TaskStatus.FINISHED);
                    runningTasks.remove(esId);
                    log.info("考试计时任务正常结束，esId: {}, 结束时间: {}", esId, endTime);
                    break;
                }

                // 每10秒检查一次（降低CPU消耗，可根据需求调整）
                TimeUnit.SECONDS.sleep(10);
            }
        } catch (InterruptedException e) {
            log.warn("考试计时任务执行被中断，esId:{}", esId);
            Thread.currentThread().interrupt(); // 恢复中断状态
        } catch (Exception e) {
            log.error("考试计时任务执行异常，esId: {}", esId, e);
            // 异常时发送结束消息
            sendExamFinishMessage(esId, stuExamInfoVo.getExamId(), stuExamInfoVo.getStudentId(), "EXCEPTION");
            timingTaskMapper.updateStatus(esId, TimingTask.TaskStatus.FINISHED);
            runningTasks.remove(esId);
        }
    }

    /**
     * 发送考试结束消息到RabbitMQ
     */
    private void sendExamFinishMessage(Long esId, Long examId, Integer studentId, String triggerType) {
        ExamFinishMessage message = new ExamFinishMessage();
        message.setEsId(esId);
        message.setExamId(examId);
        message.setStudentId(studentId);
        message.setTriggerType(triggerType);

        try {
            // 发送消息：交换机 + 路由键 + 消息体
            rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
            log.info("考试结束消息发送成功，esId: {}, 触发类型: {}", esId, triggerType);
        } catch (Exception e) {
            log.error("考试结束消息发送失败，esId: {}", esId, e);
            // 消息发送失败可添加重试机制（如本地消息表）
        }
    }

    /**
     * 应用启动时恢复未完成的任务
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 避免重复执行（Spring容器初始化完成后执行一次）
        if (event.getApplicationContext().getParent() == null) {
            log.info("开始恢复应用重启前的考试计时任务...");
            List<TimingTask> unfinishedTasks = timingTaskMapper.selectAllRunningTasks();

            for (TimingTask task : unfinishedTasks) {
                Long esId = task.getEsId();
                Date startTime = task.getStartTime();
                Integer durationMinutes = task.getDurationMinutes();
                Date endTime = DateUtils.addMinutes(startTime, durationMinutes);
                Date now = new Date();

                if (now.after(endTime)) {
                    // 已超时，直接发送结束消息
                    sendExamFinishMessage(esId, task.getExamId(), task.getStudentId(), "RECOVERY");
                    timingTaskMapper.updateStatus(esId, TimingTask.TaskStatus.FINISHED);
                    log.info("任务已超时，直接执行结束方法，esId: {}", esId);
                } else {
                    // 构建VO并恢复计时任务
                    StuExamInfoVo vo = new StuExamInfoVo();
                    vo.setEsId(esId);
                    vo.setExamId(task.getExamId());
                    vo.setStudentId(task.getStudentId());
                    vo.setStartTime(startTime);
                    vo.setDuration(durationMinutes);

                    Future<?> future = taskExecutor.submit(() -> executeExamTimingTask(vo));
                    runningTasks.put(esId, future);
                    log.info("恢复考试计时任务，esId: {}, 剩余时长: {}分钟",
                            esId, (endTime.getTime() - now.getTime()) / (1000 * 60));
                }
            }
            log.info("考试计时任务恢复完成，共恢复 {} 个任务", unfinishedTasks.size());
        }
    }

    // ------------------- 监控接口相关方法 -------------------
    /**
     * 根据esId查询任务状态
     */
    public TimingTask getTaskByEsId(Long esId) {
        return timingTaskMapper.selectByEsId(esId);
    }

    /**
     * 根据学生ID和考试ID查询任务
     */
    public TimingTask getTaskByStudentAndExam(Integer studentId, Long examId) {
        return timingTaskMapper.selectByStudentAndExam(studentId, examId);
    }

    /**
     * 查询任务剩余时长（分钟）
     */
    public Long getRemainingMinutes(Long esId) {
        TimingTask task = timingTaskMapper.selectByEsIdAndStatus(esId, TimingTask.TaskStatus.RUNNING);
        if (task == null) {
            return -1L; // 无运行中任务
        }
        Date endTime = DateUtils.addMinutes(task.getStartTime(), task.getDurationMinutes());
        Date now = new Date();
        long remainingMs = endTime.getTime() - now.getTime();
        return remainingMs > 0 ? remainingMs / (1000 * 60) : 0L;
    }
}