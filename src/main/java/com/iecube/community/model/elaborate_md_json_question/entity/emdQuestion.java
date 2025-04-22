package com.iecube.community.model.elaborate_md_json_question.entity;

import lombok.Data;

import java.util.List;

@Data
public class emdQuestion {
    private String id;
    private String stage;  // "before-class" "experiment" "after-class"
    private String question;  // 问题
    private List<String> images;    // 图片列表 imageData URL
    private String answer;    // 答案
    private String flag;      // 标签
    private String analysis;  // 题目解析
    private String misguidance;  //  错误引导
    private Integer quality;   // 题目难易程度  1-10
}
