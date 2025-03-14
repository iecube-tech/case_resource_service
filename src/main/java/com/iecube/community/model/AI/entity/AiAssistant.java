package com.iecube.community.model.AI.entity;

import lombok.Data;

@Data
public class AiAssistant {
    private Long id;
    private Integer studentId;
    private Integer taskId;
    private String chatId;
}
