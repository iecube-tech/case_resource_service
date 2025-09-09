package com.iecube.community.model.Ai2830.service;

public interface Ai2830Service {
    String getChatId(Integer studentId, Long taskId, String type, Integer version);

    Boolean chatIdExist(String chatId);
}
