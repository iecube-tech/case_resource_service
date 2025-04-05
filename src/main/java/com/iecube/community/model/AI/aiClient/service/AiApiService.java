package com.iecube.community.model.AI.aiClient.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.iecube.community.model.AI.aiClient.dto.MarkerQuestion;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

public interface AiApiService {
    String genChat();

    void webSocketConnect(String chatId);

    void webSocketDisConnect(String chatId);

    // 助教
    void useTeachingAssistant(String chatId, String bookId, String sectionPrefix, List<String> imgList, String question);

    // 提问
    void useQuestioner(String chatId, String bookId, Integer qAmount, String scene, String sectionPrefix);

    // 校验
    void useMarker(String chatId, String bookId, MarkerQuestion question, String stuAnswer, String sectionPrefix, List<String> imgList);

    JsonNode getJsonRes(String artefactId);
}
