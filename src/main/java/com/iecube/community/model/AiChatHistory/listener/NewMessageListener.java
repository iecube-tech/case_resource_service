package com.iecube.community.model.AiChatHistory.listener;

import com.iecube.community.model.AiChatHistory.aiMessageEvent.NewMessageEvent;
import com.iecube.community.model.AiChatHistory.service.AiChatHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NewMessageListener {
    private final AiChatHistoryService aiChatHistoryService;
    public NewMessageListener(AiChatHistoryService aiChatHistoryService) {
        this.aiChatHistoryService = aiChatHistoryService;
    }

    @Async
    @EventListener
    public void saveMessage(NewMessageEvent event) {
        log.info("存储对话消息：{}", event.getChatId());
        aiChatHistoryService.saveAiChat(event.getChatId(), event.getMessage(), event.getRole());
    }
}
