package com.iecube.community.model.elaborate_md_task.entity;

import lombok.Data;

import java.util.Date;

@Data
public class EMDTaskRecord {
    private Long id;
    private Integer studentId;
    private Integer taskId;
    private String type;
    private Date time;
    private Long sectionId;
    private Integer sectionSort;
    private Long blockId;
    private Integer blockSort;
    private String payload;
    private String cellId;
    private String cellData;
    private String chatId;
    private String sendMessage;
}
