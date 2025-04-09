package com.iecube.community.model.AI.aiServer.assistant;

import com.iecube.community.model.AI.aiClient.service.AiApiService;
import com.iecube.community.model.AI.aiMiddlware.WebSocketSessionManage;
import com.iecube.community.model.AI.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.HttpStatus;
import java.util.Map;

@Component
public class AiServerAssistantWebSocketInterceptor implements HandshakeInterceptor {
    @Autowired
    private WebSocketSessionManage sessionManage;

    @Autowired
    private AiService aiService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 从路径中提取ID
        String path = request.getURI().getPath();
        String chatId = path.substring(path.lastIndexOf('/') + 1);
        boolean isExist =  aiService.chatIdExist(chatId);
        if(!isExist){
            response.setStatusCode(HttpStatus.NOT_FOUND);
        }
        return isExist;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
