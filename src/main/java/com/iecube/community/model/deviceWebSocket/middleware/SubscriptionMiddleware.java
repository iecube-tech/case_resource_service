package com.iecube.community.model.deviceWebSocket.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.deviceWebSocket.dto.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
public class SubscriptionMiddleware {


    /**
     * 在线设备连接：[deviceId, session]
     */
    public final ConcurrentMap<String, WebSocketSession> deviceSessions = new ConcurrentHashMap<>();

    /**
     * 在线前端连接：[sessionId, session]
     */
    public final ConcurrentMap<String, WebSocketSession> frontSessions = new ConcurrentHashMap<>();

    /**
     * 设备订阅的前端：<设备ID, 前端session>
     */
    public final ConcurrentMap<String, WebSocketSession> deviceSubscriptions = new ConcurrentHashMap<>();

    /**
     * 前端订阅的设备：<前端sessionId, 订阅设备>
     */
    public final ConcurrentMap<String, String> sessionSubscriptions = new ConcurrentHashMap<>();

    /**
     * @param session socket会话
     * @param message 消息
     * @throws IOException 异常
     */
    public void sendMessage(WebSocketSession session, Message message){
        if(session == null || !session.isOpen()){
            log.warn("尝试向不存在或已关闭的session发送消息: {}, {}", session,message);
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            String msg = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(msg));
        }catch (Exception e){
            log.error("发送消息异常{},{}",session.getId(), e.getMessage());
        }

    }
}
