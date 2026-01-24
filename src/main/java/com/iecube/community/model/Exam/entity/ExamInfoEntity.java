package com.iecube.community.model.Exam.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName("ExamInfo")
public class ExamInfoEntity {
    private Long id;
    private Integer projectId; //关联的project
    private String name; // 考试名称
    private Integer duration; //时长， 分钟
    private Double totalScore; // 考试总分
    private Double passScore; //及格分数
    private Date startTime; //考试开始时间
    private Date endTime; // 考试结束时间
    private Boolean useRandomOption;
    private Boolean useRandomQuestion;
    private Boolean aiAutoCheck;
    private Date createTime; // 创建时间
    private Integer creator; // 创建人
    private Integer lastModifiedUser; // 最后修改人
    private Date lastModifiedTime; // 最后修改时间
    // 关联的题目列表
    private List<QuestionEntity> examQuestions;
}
