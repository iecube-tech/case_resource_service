package com.iecube.community.model.AI.aiServer;

import com.iecube.community.model.AI.aiServer.assistant.AiServerAssistantWebSocketHandler;
import com.iecube.community.model.AI.aiServer.assistant.AiServerAssistantWebSocketInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocket
public class AiServeWebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketAssistantHandler(),"/ai/server/assistant/{id}")
                .addInterceptors(webSocketAssistantInterceptor())
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler webSocketAssistantHandler() {
        return new AiServerAssistantWebSocketHandler();
    }

    @Bean
    public HandshakeInterceptor webSocketAssistantInterceptor() {
        return new AiServerAssistantWebSocketInterceptor();
    }
}
