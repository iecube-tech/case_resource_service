package com.iecube.community.model.Exam_timing.Mapper;

import com.iecube.community.model.Exam_timing.Dto.TimingTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 计时任务Mapper
 */
@Mapper
public interface TimingTaskMapper {
    /**
     * 新增任务
     */
    int insert(TimingTask task);

    /**
     * 根据esId和状态查询任务
     */
    TimingTask selectByEsIdAndStatus(@Param("esId") Long esId, @Param("status") TimingTask.TaskStatus status);

    /**
     * 查询所有运行中的任务
     */
    List<TimingTask> selectAllRunningTasks();

    /**
     * 更新任务状态
     */
    int updateStatus(@Param("esId") Long esId, @Param("status") TimingTask.TaskStatus status);

    /**
     * 根据esId查询任务详情（监控用）
     */
    TimingTask selectByEsId(@Param("esId") Long esId);

    /**
     * 查询学生的考试任务（监控用）
     */
    TimingTask selectByStudentAndExam(@Param("studentId") Integer studentId, @Param("examId") Long examId);
}