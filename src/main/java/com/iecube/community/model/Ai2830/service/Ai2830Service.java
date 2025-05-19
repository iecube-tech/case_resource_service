package com.iecube.community.model.Ai2830.service;

public interface Ai2830Service {
    String getChatId(Integer studentId, Integer taskId, String type);

    Boolean chatIdExist(String chatId);
}
