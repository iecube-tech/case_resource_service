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
    Integer status;
    Boolean subjective;
    Integer qType;
    String question;
    String args;
}
