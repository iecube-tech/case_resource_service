package com.iecube.community.model.AiChatHistory.aiMessageEvent;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewMessageEvent extends ApplicationEvent {
    private final String message;
    private final String role;
    private final String chatId;

    public NewMessageEvent(Object source, String message, String role, String chatId) {
        super(source);
        this.message = message;
        this.role = role;
        this.chatId = chatId;
    }
}
