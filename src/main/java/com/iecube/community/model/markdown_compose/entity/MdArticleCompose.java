package com.iecube.community.model.markdown_compose.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MdArticleCompose {
    Long id;
    Integer articleId;
    Integer index;
    String name;
    String val;
    String answer;
    Integer score;
    Integer result;
    @JsonProperty("subjective")
    Boolean subjective;
    @JsonProperty("qType")
    Integer qType;
    String question;
    String args;
}