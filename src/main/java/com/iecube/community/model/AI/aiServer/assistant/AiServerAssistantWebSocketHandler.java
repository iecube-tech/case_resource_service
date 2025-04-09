package com.iecube.community.model.AI.aiServer.assistant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.AI.aiClient.service.AiApiService;
import com.iecube.community.model.AI.aiMiddlware.WebSocketSessionManage;
import com.iecube.community.util.jwt.AuthUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class AiServerAssistantWebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private WebSocketSessionManage webSocketSessionManage;

    @Autowired
    private AiApiService aiApiService;

//    private final StringRedisTemplate redisTemplate;
//
//    @Autowired
//    public AiServerAssistantWebSocketHandler(StringRedisTemplate redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        URI uri = session.getUri();
        if (uri != null) {
            String path = uri.toString();
            String chatId = path.substring(path.lastIndexOf('/') + 1);
//            redisTemplate.opsForValue().set(getSessionIdRedisKey(session), AuthUtils.getCurrentUserEmail());
            session.setTextMessageSizeLimit(10485760);
            webSocketSessionManage.serverSessionManager.addSession(chatId, session); // manger 添加与前端的socket管理
            // 建立与AI服务的链接
            WebSocketSession aiSession = webSocketSessionManage.clientSessionManager.getSessionById(chatId);
            if(aiSession == null) {
                aiApiService.webSocketConnect(chatId);
                aiSession = webSocketSessionManage.clientSessionManager.getSessionById(chatId);
            }
            log.info("学生端ai对话已连接 {}, webS-->{}, aiS-->{} ",  chatId, session.getId(), aiSession.getId());
        }else {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String chatId = webSocketSessionManage.serverSessionManager.getIdBySession(session);
//        String email = redisTemplate.opsForValue().get(getSessionIdRedisKey(session));
//        redisTemplate.delete(getSessionIdRedisKey(session));
        log.info("学生端对话已断开，{}, {}, {}, {}", chatId, session.getId(), status.getCode(), status.getReason());
//        log.info("学生端对话已断开，{}, {}, {}, {}, {}",email, chatId, session.getId(), status.getCode(), status.getReason());
        webSocketSessionManage.serverSessionManager.removeSession(session);
        //断连AI端的对话连接
        WebSocketSession toCloseSession = webSocketSessionManage.clientSessionManager.getSessionById(chatId);
        if(toCloseSession!=null){
            toCloseSession.close(CloseStatus.NORMAL);
            webSocketSessionManage.clientSessionManager.removeSession(toCloseSession.getId());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
//        log.info("消息长度:{}",message.getPayload().getBytes(StandardCharsets.UTF_8).length*8);
        String chatId = webSocketSessionManage.serverSessionManager.getIdBySession(session);
        WebSocketSession toSendSession = webSocketSessionManage.clientSessionManager.getSessionById(chatId);
        if(toSendSession!=null){
            toSendSession.sendMessage(message);
        }else {
            Msg msg = new Msg();
            msg.setMsg("AI服务异常");
            msg.setType("error");
            ObjectMapper objectMapper = new ObjectMapper();
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
        }
        //todo ai服务方websocket 重连
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
