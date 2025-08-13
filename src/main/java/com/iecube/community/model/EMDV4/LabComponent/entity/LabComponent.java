package com.iecube.community.model.EMDV4.LabComponent.entity;

import lombok.Data;

@Data
public class LabComponent {
    private Long id;
    private Long tag;
    private String name;
    private String icon;
    private String type;
    private Boolean needCalculate;
    private Double totalScore;
    private Double score;
    private Double scoreProportion;
    private String style;
    private String config;
    private String payload;
}
