package com.iecube.community.model.exportProgress.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ExportProgressChild {
    private String id;
    private String exportProgressId;
    private Integer resource;
    private Integer order;
    private Boolean finished;
    private String message;
    private Date createTime;
}
