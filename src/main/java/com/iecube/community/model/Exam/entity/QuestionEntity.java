package com.iecube.community.model.Exam.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.iecube.community.model.Exam.Dto.QuesType;
import lombok.Data;

import java.util.List;

@Data
@TableName("Exam_question")
public class QuestionEntity {
    private String id;
    private Long pId;
    private QuesType quesType;
    private Integer order;
    private Boolean isRandom;
    private Integer randomNum;
    private Integer difficulty;
    private Double score;
    // 关联的题目内容列表
    private List<QuesContentEntity> examQuesContents;
}
