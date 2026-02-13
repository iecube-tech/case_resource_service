package com.iecube.community.model.Exam_timing.Dto;
import lombok.Data;
import java.util.Date;

@Data
public class TimingTask {
    private Long id;
    private Long esId;
    private Long examId;
    private Integer projectId;
    private Integer studentId;
    private Date startTime; // 任务开始时间
    private Integer durationMinutes; // 持续时长（分钟）
    private TaskStatus status; // 任务状态
    private Date createTime;
    private Date updateTime;

    /**
     * 任务状态枚举
     */
    public enum TaskStatus {
        RUNNING, FINISHED, INTERRUPTED
    }
}