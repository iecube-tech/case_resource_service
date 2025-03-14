package com.iecube.community.model.AI.aiServer.assistant;

import com.iecube.community.model.AI.aiClient.service.AiApiService;
import com.iecube.community.model.AI.aiMiddlware.WebSocketSessionManage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;

@Component
@Slf4j
public class AiServerAssistantWebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private WebSocketSessionManage webSocketSessionManage;

    @Autowired
    private AiApiService aiApiService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        URI uri = session.getUri();
        if (uri != null) {
            String path = uri.toString();
            String chatId = path.substring(path.lastIndexOf('/') + 1);
            log.info("学生端对话已连接，{}, {}", chatId, session.getId());
            webSocketSessionManage.serverSessionManager.addSession(chatId, session); // manger 添加与前端的socket管理
            // 建立与AI服务的链接
            aiApiService.webSocketConnect(chatId);
        }else {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String chatId = webSocketSessionManage.serverSessionManager.getIdBySession(session);
        log.info("学生端对话已断开，{}, {}", chatId, session.getId());
        webSocketSessionManage.serverSessionManager.removeSession(session);

        //断连AI端的对话连接
        WebSocketSession toCloseSession = webSocketSessionManage.clientSessionManager.getSessionById(chatId);
        toCloseSession.close(CloseStatus.NORMAL);

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        String chatId = webSocketSessionManage.serverSessionManager.getIdBySession(session);
        WebSocketSession toSendSession = webSocketSessionManage.clientSessionManager.getSessionById(chatId);
        toSendSession.sendMessage(message);
    }
}
