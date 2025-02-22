package com.iecube.community.model.deviceWebSocket.controller;

import com.iecube.community.model.deviceWebSocket.dto.DeviceCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MetricsController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/metrics/{deviceId}")
    public void handleMetrics(@DestinationVariable String deviceId, DeviceCommand command) {
        // 处理控制指令（未来扩展）
    }
}