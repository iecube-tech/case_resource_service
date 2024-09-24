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

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public AuthInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.warn("ip:{}", request.getHeader("X-Forwarded-For"));
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            return true;
        }

        String token = request.getHeader(AuthUtils.ACCESS_TOKE_KEY);
        if(token == null){
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        if (!StringUtils.hasText(token)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        if (AuthUtils.authed(token, redisTemplate)) {
            return true;
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "请重新登录");
        return false;
    }
}
