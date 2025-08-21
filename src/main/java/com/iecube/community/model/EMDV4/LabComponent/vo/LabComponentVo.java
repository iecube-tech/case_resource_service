package com.iecube.community.model.EMDV4.LabComponent.vo;

import lombok.Data;

@Data
public class LabComponentVo {
    private Long componentId;
    private Long tag;
    private Integer stage; // 实验阶段 前中后 012
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
    private String tagName;
}
