package com.iecube.community.model.EMDV4Project.EMDV4Analysis.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PSTDto {
    private Long pstId;
    private Long ptId;
    private Long psId;
    private Integer status;
    private String studentId;
    private String studentName;
    private String taskName;
    private Double taskClasshour;
    private Double taskWeighting;
    private Double taskScore;
    private Double taskTotalScore;
    private Double psScore;
    private Integer stage;
    private Date startTime;
    private Date endTime;
    private String bookIcon;
    private String stageIcon;
}
