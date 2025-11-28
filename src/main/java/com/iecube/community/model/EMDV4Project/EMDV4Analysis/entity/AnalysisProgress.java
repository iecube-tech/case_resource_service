package com.iecube.community.model.EMDV4Project.EMDV4Analysis.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AnalysisProgress {
    private String id;
    private Integer projectId;
    private Integer totalCount;
    private Integer completedCount;
    private Integer percent;
    private Boolean finished;
    private String message;
    private Date createTime;
    private Date updateTime;
}
