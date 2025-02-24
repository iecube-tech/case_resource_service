package com.iecube.community.model.deviceWebSocket.subscription;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.deviceWebSocket.dto.Message;
import com.iecube.community.model.deviceWebSocket.middleware.SubscriptionMiddleware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;


@Slf4j
@Component
public class SubscriptionManager {
    @Autowired
    private SubscriptionMiddleware subscriptionMiddleware;

    // 添加订阅
    public synchronized void subscribe(String deviceId, WebSocketSession session) throws IOException {
        if(deviceId==null){
            Message message = new Message();
            message.setType("ERROR");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree("{\"msg\":\"未携带参数deviceId\"}");
            message.setData(jsonNode);
            subscriptionMiddleware.sendMessage(session, message);
        }
        // 先判断设备有无被订阅
        WebSocketSession deviceSession = subscriptionMiddleware.deviceSessions.get(deviceId);
        if(deviceSession == null) {
            session.close(new CloseStatus(CloseStatus.GOING_AWAY.getCode(),"设备不在线"));
            return;
        }
        if(subscriptionMiddleware.deviceSubscriptions.containsKey(deviceId)) {
            // 设备已经被订阅
            session.close(new CloseStatus(CloseStatus.GOING_AWAY.getCode(),"设备已经被订阅"));
            return;
        }
        subscriptionMiddleware.deviceSubscriptions.put(deviceId, session);
        subscriptionMiddleware.sessionSubscriptions.put(session.getId(), deviceId);
        Message message = new Message();
        message.setType("START");
        subscriptionMiddleware.sendMessage(deviceSession,message);

        Message msg =new Message();
        msg.setType("SUCCESS");
        subscriptionMiddleware.sendMessage(session,msg);
        log.info("订阅设备{}",deviceId);
    }

    // 前端移除订阅
    public synchronized void unsubscribe(String deviceId, WebSocketSession session) throws IOException {
        WebSocketSession deviceSession = subscriptionMiddleware.deviceSessions.get(deviceId);
        Message msg = new Message();
        msg.setType("STOP");
        subscriptionMiddleware.sendMessage(deviceSession,msg);
        subscriptionMiddleware.deviceSubscriptions.remove(deviceId);
        subscriptionMiddleware.sessionSubscriptions.remove(session.getId());
        Message msg1 =new Message();
        msg1.setType("SUCCESS");
        subscriptionMiddleware.sendMessage(session,msg1);
        log.info("取消订阅{}",deviceId);
    }
}
