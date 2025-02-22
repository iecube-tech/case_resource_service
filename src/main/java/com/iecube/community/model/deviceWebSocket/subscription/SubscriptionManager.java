package com.iecube.community.model.deviceWebSocket.subscription;

import com.iecube.community.model.deviceWebSocket.handler.DeviceWebSocketHandler;
import com.iecube.community.model.deviceWebSocket.handler.FrontendWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// SubscriptionManager.java
@Slf4j
@Component
public class SubscriptionManager {
    @Autowired
    private DeviceWebSocketHandler deviceWebSocketHandler;

    @Autowired
    private FrontendWebSocketHandler frontendWebSocketHandler;

    // 设备订阅表：<设备ID, 前端session>
    private final ConcurrentMap<String, WebSocketSession> deviceSubscriptions = new ConcurrentHashMap<>();
    // 会话订阅记录：<前端sessionId, 订阅设备>
    private final ConcurrentMap<String, String> sessionSubscriptions = new ConcurrentHashMap<>();

    // 添加订阅
    public synchronized void subscribe(String deviceId, WebSocketSession session) {
        // 先判断设备有无被订阅
        if(deviceSubscriptions.containsKey(deviceId)) {
            // 设备已经被订阅
            try {
                session.sendMessage(new TextMessage("设备已被订阅"));
                session.close();
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
            return;
        }
        //再判断设备状态
        if(!deviceWebSocketHandler.deviceOnline(deviceId)) {
            //设备不在线
            try {
                session.sendMessage(new TextMessage("设备离线"));
                session.close();
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
            return;
        }
        deviceSubscriptions.put(deviceId, session);
        sessionSubscriptions.put(session.getId(), deviceId);
    }

    // 前端移除订阅
    public synchronized void unsubscribe(String deviceId, WebSocketSession session) {
        deviceSubscriptions.remove(deviceId);
        sessionSubscriptions.remove(session.getId());
    }

    // 设备离线
    public synchronized void deviceDisSubscribe(String deviceId){
        WebSocketSession frontSession = deviceSubscriptions.get(deviceId);
        deviceSubscriptions.remove(deviceId);
        sessionSubscriptions.remove(frontSession.getId());
    }

    // 会话断开时清理
    public void cleanupSession(WebSocketSession session) {
        String deviceId = sessionSubscriptions.get(session.getId());
        deviceSubscriptions.remove(deviceId);
        sessionSubscriptions.remove(session.getId());
    }

    public void sendToDevice(WebSocketSession frontSession, String message) {
        String deviceId = sessionSubscriptions.get(frontSession.getId());
        if(deviceId!=null) {
            deviceWebSocketHandler.sendToDevice(deviceId, message);
        }
    }

    public void sendToFrontend(String deviceId, String message) {
        WebSocketSession session = deviceSubscriptions.get(deviceId);
        if(session!=null) {
            frontendWebSocketHandler.sendToFront(session, message);
        }
    }
}
