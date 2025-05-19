package com.iecube.community.model.Ai2830.WebSocketConfig.handler;

import com.iecube.community.model.Ai2830.service.Ai2830Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;


/**
 * websocket 连接拦截器
 */
@Component
public class Ai2830WebSocketInterceptor implements HandshakeInterceptor {

    @Autowired
    private Ai2830Service ai2830Service;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 从路径中提取ID
        String path = request.getURI().getPath();
        String chatId = path.substring(path.lastIndexOf('/') + 1);
        boolean isExist =  ai2830Service.chatIdExist(chatId);
        if(!isExist){
            response.setStatusCode(HttpStatus.NOT_FOUND);
        }
        return isExist;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
