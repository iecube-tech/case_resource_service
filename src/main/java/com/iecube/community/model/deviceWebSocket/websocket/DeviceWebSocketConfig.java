package com.iecube.community.model.deviceWebSocket.websocket;

import com.iecube.community.model.deviceWebSocket.handler.DeviceWebSocketHandler;
import com.iecube.community.model.deviceWebSocket.handler.FrontendWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class DeviceWebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 设备端接入点
        registry.addHandler(webSocketHandlerDevice(), "/ws/device")
                .setAllowedOrigins("*");

        // 前端接入点（添加拦截器）
        registry.addHandler(webSocketHandlerFront(), "/ws/front")
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler webSocketHandlerDevice() {
        return new DeviceWebSocketHandler();
    }

    @Bean
    public WebSocketHandler webSocketHandlerFront() {
        return new FrontendWebSocketHandler();
    }
}