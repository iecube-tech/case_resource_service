package com.iecube.community.model.AI.aiClient.service;

import org.springframework.web.socket.WebSocketSession;

public interface AiApiService {
    String genChat();

    void webSocketConnect(String chatId);

    void webSocketDisConnect(String chatId);
}
