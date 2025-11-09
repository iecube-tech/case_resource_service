package com.iecube.community.model.exportProgress.dto;

import lombok.Data;

@Data
public class PstReportDTO {
    private Long pstId;
    private Long ptId;
    private Long psId;
    private String gcName;
    private String studentId;
    private String studentName;
    private String taskName;
    private Double taskWeighting;
    private Double taskScore;
    private Double taskTotalScore;
    private Double psScore;
    private String taskBookId;
}
