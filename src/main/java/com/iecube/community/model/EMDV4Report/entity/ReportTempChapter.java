package com.iecube.community.model.EMDV4Report.entity;

import lombok.Data;

@Data
public class ReportTempChapter {
    private Long id;
    private Long reportTempId;
    private ChapterType type;
    private String title;
    private Integer order;
    private Boolean required;

    public enum ChapterType{
        TEXT,IMAGE
    }
}
