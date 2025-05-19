package com.iecube.community.model.Ai2830.WebSocketConfig;

import com.iecube.community.model.Ai2830.WebSocketConfig.handler.Ai2830WebSocketInterceptor;
import com.iecube.community.model.Ai2830.WebSocketConfig.handler.Ai2830WebsocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocket2830Config implements WebSocketConfigurer {
    @Autowired
    private Ai2830WebsocketHandler ai2830WebsocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(ai2830WebsocketHandler, "/ai2830/server/assistant/{id}")
                .addInterceptors(webSocket2830Interceptor())
//                .addInterceptors(authInterceptor)
                .setAllowedOrigins("*");
    }

    @Bean
    public HandshakeInterceptor webSocket2830Interceptor() {
        return new Ai2830WebSocketInterceptor();
    }
}
