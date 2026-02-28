package com.iecube.community.model.EMDV4Report.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ReportStudent {
    private Long id;
    private Integer projectId;
    private Long reportTempId;
    private Integer studentId;
    private String status;
    private Date submitTime;
}
