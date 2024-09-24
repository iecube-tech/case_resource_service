package com.iecube.community.model.pst_article_compose.entity;

import lombok.Data;

@Data
public class PSTArticleCompose {
    Long id;
    Integer pstId;
    Integer pstArticleId;
    Integer index;
    String name;
    String val;
    String answer;
    Integer score;
    Double result;
    Integer status;  // 提交状态 0未提交 1 已提交
    Boolean subjective;
    Integer qType;
    String question;
    String args;
}
