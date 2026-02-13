package com.iecube.community.model.Exam_timing.Dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 考试计时结束消息
 */
@Data
public class ExamFinishMessage implements Serializable {
    // 建议添加序列化版本号（可选但推荐）
    @Serial
    private static final long serialVersionUID = 1L;

    private Long esId;
    private Long examId;
    private Integer studentId;
    private String triggerType; // AUTO:自动结束，MANUAL:手动中断
}