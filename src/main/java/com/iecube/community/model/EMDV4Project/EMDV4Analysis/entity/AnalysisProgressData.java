package com.iecube.community.model.EMDV4Project.EMDV4Analysis.entity;

import lombok.Data;

@Data
public class AnalysisProgressData {
    private String id;
    private String apId;
    private String type;
    private String data;
    private Long ptId;
    private Long psId;
    private String studentId;
}
