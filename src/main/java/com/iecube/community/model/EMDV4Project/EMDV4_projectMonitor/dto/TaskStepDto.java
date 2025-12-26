package com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.dto;

import lombok.Data;

@Data
public class TaskStepDto {
    private Long psId;
    private String stuId;
    private String stuName;
    private Double psScore;
    private Long pstId;
    private Long ptId;
    private String ptName;
    private Integer blockStage;
    private String blockName;
    private Integer blockStatus;
}
