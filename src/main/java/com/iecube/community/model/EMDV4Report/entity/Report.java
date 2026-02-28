package com.iecube.community.model.EMDV4Report.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Report {
    private String id;
    private Long rsId;
    private ReportTempChapter.ChapterType type;
    private String title;
    private Integer order;
    private Boolean required;
    private String content;
    private Date lastModifiedTime;
}
