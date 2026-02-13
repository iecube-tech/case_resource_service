package com.iecube.community.model.Exam_timing.consumer;

import com.iecube.community.model.Exam.Service.ExamService;
import com.iecube.community.model.Exam_timing.Dto.ExamFinishMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 考试结束消息消费端（解耦的结束方法）
 */
@Component
@Slf4j
public class ExamFinishConsumer {

    @Autowired
    private ExamService examService;

    /**
     * 消费考试结束消息，执行核心业务逻辑
     */
    @RabbitListener(queues = "${timing.task.rabbitmq.queue}")
    public void handleExamFinishMessage(ExamFinishMessage message) {
        try {
            Long esId = message.getEsId();
            Long examId = message.getExamId();
            Integer studentId = message.getStudentId();
            String triggerType = message.getTriggerType();

            log.info("开始执行考试结束业务逻辑，esId: {}, 考试ID: {}, 学生ID: {}, 触发类型: {}",
                    esId, examId, studentId, triggerType);

            // ------------------- 核心结束方法逻辑 -------------------
            // 1. 更新考试状态（如examTimeStatus为done）
            // 2. 计算耗时、自动提交试卷
            // 3. 评分（AI评分/人工评分）
            // 4. 推送考试结束通知
            // TODO: 替换为你的实际业务逻辑
            // --------------------------------------------------------
            examService.submitExam(esId);
            examService.computeScore(esId);

            log.info("考试结束业务逻辑执行完成，esId: {}", esId);
        } catch (Exception e) {
            log.error("执行考试结束业务逻辑异常", e);
            // 异常处理：重试、记录失败日志、告警等
        }
    }
}