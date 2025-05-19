package com.iecube.community.model.elaborate_md_json_question.question;

import lombok.Data;

import java.util.List;

@Data
public class emdQuestion {
    private String stage;  // "before-class" "experiment" "after-class"
    private String question;  // 问题
    private List<ChoiceOption> options;
    private List<String> images;    // 图片列表 imageData URL
    private String answer;    // 答案
    private String tag;      // 标签
    private List<ChoiceOption> answerOption;
    private String analysis;  // 题目解析
    private String hintWhenWrong;  //  错误引导
    private Integer difficulty;   // 题目难易程度  1-10
    private double score;  // 题目的成绩
}
