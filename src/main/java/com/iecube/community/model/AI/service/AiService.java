package com.iecube.community.model.AI.service;

public interface AiService {
    String getAssistantChatId(Integer studentId, Integer taskId);

    Boolean chatIdExist(String chatId);
}
