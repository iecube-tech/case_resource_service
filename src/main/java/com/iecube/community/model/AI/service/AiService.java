package com.iecube.community.model.AI.service;

import java.util.List;

public interface AiService {
    String getAssistantChatId(Integer studentId, Long taskId, String type, Integer version);

    String getStudentTaskChatId(Integer studentId, Long taskId, Integer version);

    Boolean chatIdExist(String chatId);

    List<String> getChatIdList();
}
