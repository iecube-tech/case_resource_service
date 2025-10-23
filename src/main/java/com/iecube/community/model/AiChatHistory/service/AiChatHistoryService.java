package com.iecube.community.model.AiChatHistory.service;

public interface AiChatHistoryService {

    void saveAiChat(String chatId, String message, String role);
}
