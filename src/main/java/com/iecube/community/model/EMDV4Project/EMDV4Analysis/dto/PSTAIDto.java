package com.iecube.community.model.EMDV4Project.EMDV4Analysis.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PSTAIDto {
    private Long ptId;
    private String taskName;
    private String studentId;
    private String studentName;
    private String chatId;
    private String message;
    private String role;
    private Date createTime;
}
