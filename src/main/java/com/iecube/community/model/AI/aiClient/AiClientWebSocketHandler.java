package com.iecube.community.model.AI.aiClient;

import com.iecube.community.model.AI.aiMiddlware.WebSocketSessionManage;
import com.iecube.community.model.AI.ex.AiAPiResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Timer;

@Component
@Slf4j
public class AiClientWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private WebSocketSessionManage webSocketSessionManage;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        log.info("连接到AI服务方-->{}",session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String chatId = webSocketSessionManage.clientSessionManager.getIdBySession(session);
        webSocketSessionManage.clientSessionManager.removeSession(session);
        log.info("与AI服务方socket连接断开：{}, {}, {},{}", chatId, session.getId(),status.getCode() ,status.getReason());
        if(status.getCode()!=CloseStatus.NORMAL.getCode()){
            log.error("与AI服务连接异常断开：{}，{}",status.getCode(),status.getReason());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        try{
//            log.info("消息长度:{}",message.getPayload().getBytes(StandardCharsets.UTF_8).length*8);
            String chatId = webSocketSessionManage.clientSessionManager.getIdBySession(session);
            WebSocketSession toSendSession = webSocketSessionManage.serverSessionManager.getSessionById(chatId);
            if(toSendSession != null) {
                toSendSession.sendMessage(message);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }

}
