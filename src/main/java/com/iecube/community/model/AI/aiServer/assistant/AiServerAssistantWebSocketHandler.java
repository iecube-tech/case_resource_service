package com.iecube.community.model.AI.aiServer.assistant;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AiServerAssistantWebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, WebSocketSession> W6ChatIdToSession;
    private final ConcurrentHashMap<String, WebSocketSession> FrontChatIdToSession;
    private final ConcurrentHashMap<String, String> FrontSessionIdToChatId;
    private final BlockingQueue<String> FrontNewConnectChatId;
    private final BlockingQueue<WebSocketSession> FrontClosedSession;
    @Autowired
    public AiServerAssistantWebSocketHandler(ConcurrentHashMap<String, WebSocketSession> W6ChatIdToSession,
                                             ConcurrentHashMap<String, WebSocketSession> FrontChatIdToSession,
                                             ConcurrentHashMap<String, String> FrontSessionIdToChatId,
                                             BlockingQueue<String> FrontNewConnectChatId,
                                             BlockingQueue<WebSocketSession> FrontClosedSession) {
        this.W6ChatIdToSession = W6ChatIdToSession;
        this.FrontChatIdToSession = FrontChatIdToSession;
        this.FrontSessionIdToChatId = FrontSessionIdToChatId;
        this.FrontNewConnectChatId = FrontNewConnectChatId;
        this.FrontClosedSession = FrontClosedSession;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        URI uri = session.getUri();
        if (uri != null) {
            String path = uri.toString();
            String chatId = path.substring(path.lastIndexOf('/') + 1);
            session.setTextMessageSizeLimit(10485760);
            // 判断之前有没有这个chatId的连接，有则关闭 并不许重连
            WebSocketSession oldSession = FrontChatIdToSession.get(chatId);
            if (oldSession != null) {
                oldSession.close(CloseStatus.GOING_AWAY);
                TimeUnit.MILLISECONDS.sleep(100);
            }
            FrontChatIdToSession.put(chatId, session);
            FrontSessionIdToChatId.put(session.getId(), chatId); // manger 添加与前端的socket管理
            FrontNewConnectChatId.put(chatId);   // 建立与AI服务的连接 生产 chatId
            log.info("online websocket: {}, webS-->{}, ",  chatId, session.getId());
        }else {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String chatId = FrontSessionIdToChatId.get(session.getId());
        // 判断chatId 是否为新session
        if(FrontChatIdToSession.get(chatId) == session){
            FrontChatIdToSession.remove(chatId);
        }
        FrontSessionIdToChatId.remove(session.getId());
        log.info("online websocket: {} closed，{}, {}, {}", chatId, session.getId(), status.getCode(), status.getReason());
        FrontClosedSession.put(session); //学生端对话断开生产者
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
//        log.info("消息长度:{}",message.getPayload().getBytes(StandardCharsets.UTF_8).length*8);
        String chatId = FrontSessionIdToChatId.get(session.getId());
        WebSocketSession toSendSession = W6ChatIdToSession.get(chatId);
        if(toSendSession!=null){
            toSendSession.sendMessage(new PingMessage());
        }else {
            Msg msg = new Msg();
            msg.setMsg("AI服务异常");
            msg.setType("error");
            ObjectMapper objectMapper = new ObjectMapper();
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
        }
    }

    private String getSessionIdRedisKey(WebSocketSession session) {
        return "FRONT_AI_SOCKET_SESSION_ID_"+session.getId();
    }

    @Data
    private static class Msg{
        private String type;
        private String msg;
    }
}
