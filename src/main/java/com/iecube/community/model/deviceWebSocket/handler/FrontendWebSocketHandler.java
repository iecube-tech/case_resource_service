package com.iecube.community.model.deviceWebSocket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.deviceWebSocket.dto.FrontData;
import com.iecube.community.model.deviceWebSocket.dto.Message;
import com.iecube.community.model.deviceWebSocket.subscription.SubscriptionManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
public class FrontendWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private SubscriptionManager subscriptionManager;
    // 使用ConcurrentHashMap维护在线会话<sessionId,session>
    private static final ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) { //连接建立
        sessions.put(session.getId(), session);
        log.info("前端连接建立: {}", session.getId());
    }

    // FrontendWebSocketHandler.java
    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        FrontData frontData = new FrontData();
        JsonNode jsonNode = objectMapper.readTree(message.getPayload());
        frontData.setDeviceId(jsonNode.get("deviceId").asText());
        frontData.setType(jsonNode.get("type").asText());
        frontData.setData(jsonNode.get("data"));
        if(frontData.getType() == null){
            session.sendMessage(new TextMessage("未携带参数：type"));
        }
        switch(frontData.getType()) {
            case "SUBSCRIBE":
                subscriptionManager.subscribe(frontData.getDeviceId(), session);
                break;
            case "UNSUBSCRIBE":
                subscriptionManager.unsubscribe(frontData.getDeviceId(), session);
                break;
            case "SEND":
                Message msg = new Message();
                msg.setType("DATA");
                msg.setData(frontData.getData());
                subscriptionManager.sendToDevice(session,objectMapper.writeValueAsString(msg));
            case "PING":
                Message msg1 = new Message();
                msg1.setType("PONG");
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg1)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NotNull CloseStatus status) {
        sessions.remove(session.getId());
        log.info("前端连接关闭: {}", session.getId());
    }


    // 广播实时数据到前端（由设备数据处理器调用）
    public void sendToFront(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            log.error("数据推送失败", e);
        }
    }
}