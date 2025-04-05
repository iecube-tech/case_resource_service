package com.iecube.community.model.AI.aiClient.dto;

import lombok.Data;

import java.util.List;

@Data
public class MarkerQuestion {
    private String analysis;
    private String answer;
    private String hint_when_wrong;
    private String id;
    private List<String> images;
    private String question;
    private String stage;
    private String tag;
}
