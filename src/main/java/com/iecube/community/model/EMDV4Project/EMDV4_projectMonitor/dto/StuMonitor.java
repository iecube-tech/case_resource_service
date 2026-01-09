package com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StuMonitor {
    private Long psId;
    private Double psScore;
    private String stuId;
    private String stuName;
    private Long pstId;
    private Long ptId;
    private String ptName;
    private Integer blockStage;
    private Integer blockLevel;
    private Integer blockOrder;
    private String blockName;
    private Integer blockStatus;
    private Date blockStartTime;
    private Date blockEndTime;
    private Double blockScore;
    private Double blockTotalScore;
    private Double blockWeighting;
}
