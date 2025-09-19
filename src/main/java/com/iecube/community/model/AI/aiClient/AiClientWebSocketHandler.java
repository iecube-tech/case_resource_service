package com.iecube.community.model.AI.aiClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.AiMessage.entity.AiMessage;
import com.iecube.community.model.AiMessage.mapper.AiMessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class AiClientWebSocketHandler extends TextWebSocketHandler {

    ConcurrentHashMap<String, WebSocketSession> W6ChatIdToSession;
    ConcurrentHashMap<String, String> W6SessionIdToChatId;
    ConcurrentHashMap<String, WebSocketSession> FrontChatIdToSession;
    ObjectMapper objectMapper;
    AiMessageMapper aiMessageMapper;

    private AtomicLong lastSentTime;

    @Autowired
    public AiClientWebSocketHandler(
                                    ConcurrentHashMap<String, WebSocketSession> W6ChatIdToSession,
                                    ConcurrentHashMap<String, String> W6SessionIdToChatId,
                                    ConcurrentHashMap<String, WebSocketSession> FrontChatIdToSession,
                                    ObjectMapper objectMapper,
                                    AiMessageMapper aiMessageMapper) {
        super();
        this.W6ChatIdToSession = W6ChatIdToSession;
        this.W6SessionIdToChatId = W6SessionIdToChatId;
        this.FrontChatIdToSession = FrontChatIdToSession;
        this.objectMapper = objectMapper;
        this.aiMessageMapper = aiMessageMapper;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        long timeout = 63000; // 超时时间（毫秒）3个心跳的时间
        this.lastSentTime = new AtomicLong(System.currentTimeMillis());
        // 定时任务，每秒检查一次
        scheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - this.lastSentTime.get() > timeout) {
                if (session.isOpen()) {
                    try {
                        String chatId = W6SessionIdToChatId.get(session.getId());
                        session.close(CloseStatus.NORMAL);
                        log.info("w6 webSocket: {} long time no Msg closed",chatId);
                    } catch (IOException e) {
                        log.error("w6 webSocket: {} long time no Msg closed error",W6SessionIdToChatId.get(session.getId()),e);
                    }
                }
                scheduler.shutdown(); // 关闭调度器
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String chatId = W6SessionIdToChatId.get(session.getId());
        W6SessionIdToChatId.remove(session.getId());
        if(W6ChatIdToSession.get(chatId) == session){
            W6ChatIdToSession.remove(chatId);
        }
        if(FrontChatIdToSession.get(chatId)!=null){
            // 判断前端是否有连接存在， 如果存在则断掉
            FrontChatIdToSession.get(chatId).close(CloseStatus.GOING_AWAY);
        }
        log.info("w6 webSocket: {} closed：{}, {}, {}", chatId, session.getId(),status.getCode() ,status.getReason());
        if(status.getCode()!=CloseStatus.NORMAL.getCode()){
            log.error("w6 webSocket: {} closed error：{}，{}",chatId,status.getCode(),status.getReason());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        this.lastSentTime = new AtomicLong(System.currentTimeMillis());
        try{
//            log.info("消息长度:{}",message.getPayload().getBytes(StandardCharsets.UTF_8).length*8);
            String chatId = W6SessionIdToChatId.get(session.getId());
            WebSocketSession toSendSession = FrontChatIdToSession.get(chatId);
            if(toSendSession != null) {
                toSendSession.sendMessage(message);
//                log.info("转发W6MSG:{}",chatId);
                //存储消息
//                System.out.println(message.getPayload());
                JsonNode msgNode = objectMapper.readTree(message.getPayload());
                if(msgNode.get("type").asText().equals("message-ack")){
                    AiMessage aiMessage = new AiMessage();
                    aiMessage.setId(msgNode.get("payload").get("id").asText());
                    aiMessage.setChatId(msgNode.get("payload").get("agent_request").get("chat_id").asText());
                    aiMessage.setRole(msgNode.get("payload").get("role").asText());
                    aiMessage.setContent(msgNode.get("payload").get("content").asText());
                    aiMessage.setCreateTime(Instant.now());
                    aiMessageMapper.insert(aiMessage);
                }
            }else {
                log.warn("无法转发W6MSG:{}",chatId);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
        this.lastSentTime = new AtomicLong(System.currentTimeMillis());
    }
}
