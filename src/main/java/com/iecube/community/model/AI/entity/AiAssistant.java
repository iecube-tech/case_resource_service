package com.iecube.community.model.AI.entity;

import lombok.Data;

@Data
public class AiAssistant {
    private Long id;
    private Integer studentId;
    private Long taskId;
    private String chatId;
    private String type;
    private Integer version;
}
