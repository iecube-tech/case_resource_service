package com.iecube.community.model.exportProgress.dto;

import lombok.Data;

@Data
public class PstReportCommentDto {
    private Integer blockStage;
    private Integer blockLevel;
    private Integer blockOrder;
    private Integer comOrder;
    private String name;
    private String type;
    private Double totalScore;
    private Double score;
    private String payload;
}
