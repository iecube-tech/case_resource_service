package com.iecube.community.model.EMDV4Report.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ReportTemp {
    private Long id;
    private Integer projectId;
    private String name;
    private Integer chapterSize;
    private TempStatus status;
    private Date createTime;
    private Integer creator;
    private Integer lastModifiedUser;
    private Date lastModifiedTime;

    public enum TempStatus {
        SAVED, PUBLISH
    }
}
