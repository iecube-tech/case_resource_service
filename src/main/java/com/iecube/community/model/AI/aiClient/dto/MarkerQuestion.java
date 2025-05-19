package com.iecube.community.model.AI.aiClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MarkerQuestion {
    private String id; //* 必须
    private String stage;  //* 必须
    private String question; //* 必须
    private List<String> images;
    private String answer; //* 必须
    private String tag;
    private String analysis;
    private String hint_when_wrong;
}
