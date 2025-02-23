package com.iecube.community.model.deviceWebSocket.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.deviceWebSocket.dto.DeviceData;
import com.iecube.community.model.deviceWebSocket.dto.Message;
import com.iecube.community.model.deviceWebSocket.middleware.SubscriptionMiddleware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class DeviceWebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private SubscriptionMiddleware subscriptionMiddleware;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // 1. 解析设备数据
        DeviceData data = parseMessage(session, message.getPayload());
        if(data.getDeviceId() == null || data.getType() == null) {
            log.warn("设备消息参数错误");
            Message msg = new Message();
            msg.setType("ERROR");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree("{\"msg\":\"设备消息参数错误\"}");
            msg.setData(jsonNode);
            subscriptionMiddleware.sendMessage(session,msg);
            return;
        }
        if(!subscriptionMiddleware.deviceSessions.containsKey(data.getDeviceId())) {
            subscriptionMiddleware.deviceSessions.put(data.getDeviceId(), session);
        }
        log.info("设备：{}",data.getDeviceId());
        switch(data.getType()) {
            case "INIT":
                log.info("INIT");
                break;
            case "DATA":
                log.info("发送数据");
                // 广播实时数据到前端（由设备数据处理器调用）
                Message msg = new Message();
                msg.setType("DATA");
                msg.setData(data.getData());
                if(data.getData()!=null){
                    WebSocketSession frontSession = subscriptionMiddleware.deviceSubscriptions.get(data.getDeviceId());
                    subscriptionMiddleware.sendMessage(frontSession, msg);
                }
                break;
            case "PING":
                log.info("PING");
                Message msg1 = new Message();
                msg1.setType("PONG");
                subscriptionMiddleware.sendMessage(session,msg1);
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        // 设备断连
        for(ConcurrentHashMap.Entry<String, WebSocketSession> entry:subscriptionMiddleware.deviceSessions.entrySet()){
            if(entry.getValue().equals(session)){
                log.info(entry.getKey(),entry.getValue());
                log.info("{}掉线",entry.getKey());
                // 若相等，获取订阅的前端session
                WebSocketSession frontSession = subscriptionMiddleware.deviceSubscriptions.get(entry.getKey());
                // 前端session断连
                if(frontSession!=null){
                    try {
                        CloseStatus closeStatus = new CloseStatus(CloseStatus.BAD_DATA.getCode(), "设备掉线");
                        frontSession.close(closeStatus);
                        log.info("通告并断连订阅方{}",frontSession.getId());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if(subscriptionMiddleware.frontSessions.containsKey(frontSession.getId())){
                        subscriptionMiddleware.frontSessions.remove(frontSession.getId());
                    }
                    if(subscriptionMiddleware.sessionSubscriptions.containsKey(frontSession.getId())){
                        subscriptionMiddleware.sessionSubscriptions.remove(frontSession.getId());
                    }
                }
                if(subscriptionMiddleware.deviceSubscriptions.containsKey(entry.getKey())){
                    subscriptionMiddleware.deviceSubscriptions.remove(entry.getKey());
                }
                // 则删除该键值对
                subscriptionMiddleware.deviceSessions.remove(entry.getKey());
            }
        }
    }

    private DeviceData parseMessage(WebSocketSession session, String payload) throws IOException {
        DeviceData deviceData = new DeviceData();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode json = objectMapper.readTree(payload);
            deviceData.setDeviceId(json.get("deviceId")==null?null:json.get("deviceId").asText());
            deviceData.setType(json.get("type")==null?null:json.get("type").asText());
            deviceData.setData(json.get("data")==null?null:json.get("data"));
        } catch (JsonProcessingException e) {
            log.warn("解析数据异常");
        }
        return deviceData;
    }
}
