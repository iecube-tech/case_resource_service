package com.iecube.community.model.deviceWebSocket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.deviceWebSocket.dto.FrontData;
import com.iecube.community.model.deviceWebSocket.dto.Message;
import com.iecube.community.model.deviceWebSocket.middleware.SubscriptionMiddleware;
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

@Component
@Slf4j
public class FrontendWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private SubscriptionMiddleware subscriptionMiddleware;

    @Autowired
    private SubscriptionManager subscriptionManager;
    // 使用ConcurrentHashMap维护在线会话<sessionId,session>

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) { //连接建立
        subscriptionMiddleware.frontSessions.put(session.getId(), session);
        log.info("前端连接建立: {}", session.getId());
    }

    // FrontendWebSocketHandler.java
    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        FrontData frontData = new FrontData();
        JsonNode jsonNode = objectMapper.readTree(message.getPayload());
        frontData.setDeviceId(jsonNode.get("deviceId")==null?null:jsonNode.get("deviceId").asText());
        frontData.setType(jsonNode.get("type")==null?null:jsonNode.get("type").asText());
        frontData.setData(jsonNode.get("data")==null?null:jsonNode.get("data"));
//        log.info("前端：{} 消息",session.getId());
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
            case "INIT":
                Message initMsg = new Message();
                initMsg.setType("INIT");
                initMsg.setData(frontData.getData());
                // 获取设备session
                String deviceId = subscriptionMiddleware.sessionSubscriptions.get(session.getId());
                WebSocketSession deviceSession = subscriptionMiddleware.deviceSessions.get(deviceId);
                log.info("浏览器向设备初始化数据-->{},{}",deviceId, deviceSession.getId());
                subscriptionMiddleware.sendMessage(deviceSession, initMsg);
                break;
            case "DATA":
                Message msg = new Message();
                msg.setType("DATA");
                msg.setData(frontData.getData());
                // 获取设备session
                String deviceId2 = subscriptionMiddleware.sessionSubscriptions.get(session.getId());
                WebSocketSession deviceSession2 = subscriptionMiddleware.deviceSessions.get(deviceId2);
                // 发送给设备
                log.info("前端：发送数据-->{},{}", deviceId2, deviceSession2.getId());
                subscriptionMiddleware.sendMessage(deviceSession2, msg);
                break;
            case "PING":
//                log.info("PING");
                Message msg1 = new Message();
                msg1.setType("PONG");
                subscriptionMiddleware.sendMessage(session, msg1);
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NotNull CloseStatus status) throws IOException {
        // 前端断开连接
        String deviceId = subscriptionMiddleware.sessionSubscriptions.get(session.getId()); // 获取设备id
        if(deviceId!=null){
            subscriptionManager.unsubscribe(deviceId,session); // 设备取消订阅
        }
        if(subscriptionMiddleware.sessionSubscriptions.containsKey(session.getId())){
            subscriptionMiddleware.sessionSubscriptions.remove(session.getId());
        }
        if(deviceId!=null && subscriptionMiddleware.deviceSubscriptions.containsKey(deviceId)){
            subscriptionMiddleware.deviceSubscriptions.remove(deviceId);
        }
        if(subscriptionMiddleware.frontSessions.containsKey(session.getId())){
            subscriptionMiddleware.frontSessions.remove(session.getId());
        }
        log.info("前端连接关闭: {}", session.getId());
    }

}