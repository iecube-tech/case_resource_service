package com.iecube.community.model.Exam.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ExamCourseVo {
    private Integer projectId;
    private String projectName;
    private String semester;
    private String cover;
    private Date createTime;
    private Integer stuNum;
}
