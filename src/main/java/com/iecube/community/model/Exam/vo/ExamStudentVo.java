package com.iecube.community.model.Exam.vo;

import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
public class ExamStudentVo {
    private Long esId;
    private Long examId;
    private String stuId;
    private String stuName;
    private Double score;
    private Double aiScore;
    private Date startTime;
    private Date endTime;
    private String status;
    private Long timeSpent;
    private String remark;

    public void setRemark(String remark) {
        this.remark = Objects.requireNonNullElse(remark, "");
    }
}
