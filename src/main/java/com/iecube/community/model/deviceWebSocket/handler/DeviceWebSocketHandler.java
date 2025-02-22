package com.iecube.community.model.deviceWebSocket.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.deviceWebSocket.dto.DeviceData;
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
public class DeviceWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private SubscriptionManager subscriptionManager;

    private static final ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) throws IOException {
        // 1. 解析设备数据
        DeviceData data = parseMessage(session, message.getPayload());
        if(data.getDeviceId() == null || data.getType() == null) {
            session.close(new CloseStatus(CloseStatus.BAD_DATA.getCode(),"携带数据异常"));
            return;
        }
        if(!sessions.containsKey(data.getDeviceId())) {
            sessions.put(session.getId(), session);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        switch(data.getType()) {
            case "SEND":
                Message msg = new Message();
                msg.setType("DATA");
                msg.setData(data.getData());
                if(data.getData()!=null){
                    subscriptionManager.sendToFrontend(data.getDeviceId(),objectMapper.writeValueAsString(msg));
                }
            case "PING":
                Message msg1 = new Message();
                msg1.setType("PONG");
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg1)));
        }
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessions.forEach((key,val)->{
            if (val.equals(session)) {
                // 若相等，则删除该键值对
                sessions.remove(key);
                subscriptionManager.deviceDisSubscribe(key);
            }
        });

    }

    public Boolean deviceOnline(String deviceId) {
        WebSocketSession session = sessions.get(deviceId);
        if(session != null) {
            return session.isOpen();
        }
        else {
            return false;
        }
    }

    private DeviceData parseMessage(WebSocketSession session, String payload) {
        DeviceData deviceData = new DeviceData();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode json = objectMapper.readTree(payload);
            deviceData.setDeviceId(json.get("deviceId").asText());
            deviceData.setType(json.get("type").asText());
            deviceData.setData(json.get("data"));
        } catch (JsonProcessingException e) {
            // todo 向客户端发送消息异常
        }
        return deviceData;
    }

    public void sendToDevice(String deviceId, String message) {
        WebSocketSession session = sessions.get(deviceId);
        if(session != null) {
            try{
                session.sendMessage(new TextMessage(message));
            }catch (IOException e){
                log.error(e.getMessage());
            }
        }
    }
}
