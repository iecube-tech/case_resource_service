package com.iecube.community.model.Exam.vo;

import lombok.Data;

import java.util.Date;

@Data
public class StuExamInfoVo {
    private Long esId;
    private Long examId;
    private Integer projectId;
    private String projectName;
    private String semester;
    private String examName;
    private Integer duration; //时长， 分钟
    private Double totalScore; // 考试总分
    private Double passScore; // 及格分数
    private Date startTime; //开始时间
    private Date endTime; // 结束时间
    private Date examStartTime; // 考试开始时间
    private Date examEndTime; // 考试结束时间
    private String examTimeStatus; // notStart doing dong
    private String examSubmitStatus; // notStart doing done
    private Double timeSpent;
    private Integer studentId;
    private Double score;
    private Double aiScore;
}
