package com.iecube.community.model.AI.aiClient.dto;

import lombok.Data;

import java.util.List;

@Data
public class MarkerQuestion {
    private String analysis;
    private String answer; //* 必须
    private String hint_when_wrong;
    private String id; //* 必须
    private List<String> images;
    private String question; //* 必须
    private String stage;  //* 必须
    private String tag;
}
