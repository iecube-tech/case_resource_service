package com.iecube.community.model.Exam.Dto;

import com.iecube.community.model.Exam.entity.ExamStudent;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ExamWithStudentDto {
    private Long id;
    private Integer projectId; //关联的project
    private String projectName; //关联的projectName
    private String semester;
    private String name; // 考试名称
    private Integer duration; //时长， 分钟
    private Double totalScore; // 考试总分
    private Double passScore;
    private Date startTime; //考试开始时间
    private Date endTime; // 考试结束时间
    private List<ExamStudent> examStudentList;
}
