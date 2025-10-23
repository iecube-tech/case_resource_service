package com.iecube.community.model.AiChatHistory.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AiChatHistory {
    private Long id;
    private String chatId;
    private String message;
    private String role;
    private Date createTime;
}