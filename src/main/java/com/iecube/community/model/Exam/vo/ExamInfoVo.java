package com.iecube.community.model.Exam.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ExamInfoVo {
    private Long examId;
    private Integer projectId;
    private String projectName;
    private String semester;
    private String examName;
    private Integer duration; //时长， 分钟
    private Double totalScore; // 考试总分
    private Double passScore; // 及格分数
    private Date startTime; //考试开始时间
    private Date endTime; // 考试结束时间
    private Integer stuNum;
    private Integer doneNum;
    private Integer doingNum;
    private Integer notStartedNum;
    private Double avgScore;
    private Double maxScore;
    private Double passRate;
}

