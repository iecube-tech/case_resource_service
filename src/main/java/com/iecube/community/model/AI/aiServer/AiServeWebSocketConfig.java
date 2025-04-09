package com.iecube.community.model.AI.aiServer;

import com.iecube.community.interceptor.AuthInterceptor;
import com.iecube.community.model.AI.aiServer.assistant.AiServerAssistantWebSocketHandler;
import com.iecube.community.model.AI.aiServer.assistant.AiServerAssistantWebSocketInterceptor;
import com.iecube.community.model.AI.aiServer.assistant.AiServiceAssistantAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocket
public class AiServeWebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketAssistantHandler(),"/ai/server/assistant/{id}")
                .addInterceptors(webSocketAssistantInterceptor())
//                .addInterceptors(authInterceptor)
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

//    @Bean
//    public HandshakeInterceptor authInterceptor() {
//        return new AiServiceAssistantAuthInterceptor(redisTemplate);
//    }

//    @Autowired
//    private AiServiceAssistantAuthInterceptor authInterceptor;
}
