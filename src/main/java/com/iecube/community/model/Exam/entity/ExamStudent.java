package com.iecube.community.model.Exam.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ExamStudent {
    private Long id;
    private Long examId;
    private Integer studentId;
    private Double score;
    private Double aiScore;
    private Date startTime;
    private Date endTime;
}
