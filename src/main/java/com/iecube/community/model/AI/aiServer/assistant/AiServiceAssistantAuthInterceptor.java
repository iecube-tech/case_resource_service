package com.iecube.community.model.AI.aiServer.assistant;

import com.iecube.community.util.jwt.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class AiServiceAssistantAuthInterceptor implements HandshakeInterceptor {
    private final StringRedisTemplate redisTemplate;

    public AiServiceAssistantAuthInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        System.out.println(request.toString());
        HttpHeaders headers = request.getHeaders();
        String protocol = headers.getFirst("sec-websocket-protocol");
        if(protocol==null || protocol.isEmpty()){
            return false;
        }
        String[] protocols = protocol.split(",");
        if(protocols.length<2){
            return false;
        }
        String token = protocols[0].trim();
        String type = protocols[1].trim();
        String agent = "Browser";
        // 这里可以添加具体的认证逻辑，比如验证 token 是否有效
        if(token == null || type == null){
            return false;
        }
        if (!StringUtils.hasText(token) || !StringUtils.hasText(type) ) {
            return false;
        }
        if (AuthUtils.authed(token, type, agent, redisTemplate)) {
            request.getHeaders().remove("sec-websocket-protocol");
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
