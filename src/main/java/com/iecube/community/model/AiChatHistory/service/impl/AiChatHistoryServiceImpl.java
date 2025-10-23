package com.iecube.community.model.AiChatHistory.service.impl;

import com.iecube.community.model.AiChatHistory.entity.AiChatHistory;
import com.iecube.community.model.AiChatHistory.mapper.AiChatHistoryMapper;
import com.iecube.community.model.AiChatHistory.service.AiChatHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AiChatHistoryServiceImpl implements AiChatHistoryService {

    @Autowired
    private AiChatHistoryMapper aiChatHistoryMapper;

    @Override
    public void saveAiChat(String chatId, String message, String role) {
        AiChatHistory msg = new AiChatHistory();
        msg.setChatId(chatId);
        msg.setMessage(message);
        msg.setRole(role);
        msg.setCreateTime(new Date());
        aiChatHistoryMapper.insert(msg);
    }
}
