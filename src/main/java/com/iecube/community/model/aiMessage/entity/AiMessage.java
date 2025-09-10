package com.iecube.community.model.aiMessage.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class AiMessage {
    private String id;
    private String chatId;
    private String role;
    private String content;
    private Instant createTime;
}
