package com.iecube.community.interceptor;

import com.iecube.community.util.jwt.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate redisTemplate;
    private final List<String> clientAgents= Arrays.asList("iecube3835");

    @Autowired
    public AuthInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        log.warn("ip:{} ==> {}", request.getHeader("X-Forwarded-For"), request.getHeader("User-Agent"));
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            return true;
        }
        String type = request.getHeader(AuthUtils.ACCESS_TYPE_KEY);
        String token = request.getHeader(AuthUtils.ACCESS_TOKE_KEY);
        String userAgent = request.getHeader("User-Agent");
        String agent = "Browser";
        if(clientAgents.contains(userAgent)){
            agent=userAgent;
        }
        if(token == null || type == null){
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        if (!StringUtils.hasText(token) || !StringUtils.hasText(type) ) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        if (AuthUtils.authed(token, type, agent, redisTemplate)) {
            return true;
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "请重新登录");
        return false;
    }
}
