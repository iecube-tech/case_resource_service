package com.iecube.community.model.EMDV4Project.EMDV4_component.entity;

import lombok.Data;

@Data
public class EMDV4Component {
    private String id;
    private String blockId;
    private Long tag;
    private Integer stage;
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
    private Integer status;
    private Integer order;
}
