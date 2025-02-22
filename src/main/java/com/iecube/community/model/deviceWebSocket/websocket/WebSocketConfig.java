package com.iecube.community.model.deviceWebSocket.websocket;

import com.iecube.community.model.deviceWebSocket.handler.DeviceWebSocketHandler;
import com.iecube.community.model.deviceWebSocket.handler.FrontendWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private FrontendWebSocketHandler frontendWebSocketHandler;

    @Autowired
    private DeviceWebSocketHandler deviceWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 设备端接入点
        registry.addHandler(deviceWebSocketHandler, "/device-connect")
                .setAllowedOrigins("*");

        // 前端接入点（添加拦截器）
        registry.addHandler(frontendWebSocketHandler, "/dashboard")
                .setAllowedOrigins("*");
    }
}