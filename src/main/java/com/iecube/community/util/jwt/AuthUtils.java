package com.iecube.community.util.jwt;

import com.iecube.community.util.ex.SystemException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.auth.service.ex.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 网络层工具类
 *
 * @author panghaoyue
 */
@Slf4j
public class AuthUtils {

    public static final String ACCESS_TOKE_KEY = "x-access-token";
    public static final String APP_CODE_SECRET = "app-code";

    private static final String SECRET = "aksjdflajs";
    private static final String USER_REDIS_KEY_PIX = "USER_";
    private static final String USER_TOKEN_REDIS_KEY_PIX = "USER_TOKEN_";

    private static final ThreadLocal<CurrentUser> LOCAL_USER = new ThreadLocal<>();
    private static final ThreadLocal<String> LOCAL_APP = new ThreadLocal<>();


    public AuthUtils() {
    }

    public String createToken(Integer userId, String email, String type) {
        HashMap claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("email", email);
        claims.put("user_type", type);
        return new JwtUtil().createToken(claims);
    }

    public static void rm(StringRedisTemplate redisTemplate) {
        redisTemplate.delete(getUserRedisKey(getCurrentUserId(), getCurrentUserType()));
        redisTemplate.delete(getUserTokenRedisKey(getCurrentUserId(), getCurrentUserType()));
    }

    public static void cache(CurrentUser currentUser, String token, StringRedisTemplate redisTemplate) {
        try {
            redisTemplate.opsForValue().set(getUserRedisKey(currentUser.getId(), currentUser.getUserType()),
                    new ObjectMapper().writeValueAsString(currentUser), 180, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.error("转JSON失败", e);
            throw new SystemException();
        }
        redisTemplate.opsForValue().set(getUserTokenRedisKey(currentUser.getId(), currentUser.getUserType()), token, 180, TimeUnit.MINUTES);
    }

    public static void setCurrentUser(Integer id, String type, StringRedisTemplate redisTemplate) {
        CurrentUser user = getAuthInfo(id, type, redisTemplate);
        LOCAL_USER.set(user);
    }

    public static CurrentUser getAuthInfo(Integer id, String type, StringRedisTemplate redisTemplate) {
        String userJson = redisTemplate.opsForValue().get(getUserRedisKey(id,type));
        try {
            return new ObjectMapper().readValue(userJson, CurrentUser.class);
        } catch (IOException e) {
            log.error("解析JSON失败", e);
            throw new SystemException();
        }
    }

    public static boolean authed(String token, StringRedisTemplate redisTemplate) {
        Integer id;
        String type;
        try {
            id = (Integer) new JwtUtil().getClaims(token).get("id");
            type = (String) new JwtUtil().getClaims(token).get("user_type");
        } catch (Exception e) {
            return false;
        }
        String t = redisTemplate.opsForValue().get(getUserTokenRedisKey(id,type));
        if (t != null && t.equals(token)) {
            setCurrentUser(id,type, redisTemplate);
            flushExpireTime(redisTemplate);
            return true;
        }
        return false;
    }

    public static void flushExpireTime(StringRedisTemplate redisTemplate) {
        if(redisTemplate.getExpire(getUserRedisKey(getCurrentUserId(), getCurrentUserType()))< 3600
                | redisTemplate.getExpire(getUserTokenRedisKey(getCurrentUserId(), getCurrentUserType()))<3600){
            redisTemplate.expire(getUserRedisKey(getCurrentUserId(), getCurrentUserType()), 180, TimeUnit.MINUTES);
            redisTemplate.expire(getUserTokenRedisKey(getCurrentUserId(), getCurrentUserType()), 180, TimeUnit.MINUTES);
        }
    }

    public static CurrentUser getCurrentUser() {
        CurrentUser userDTO = LOCAL_USER.get();
        if (userDTO == null) {
            throw new AuthException();
        }
        return userDTO;
    }

    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    public static Integer getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static String getCurrentUserType(){
        return getCurrentUser().getUserType();
    }

    private static String getUserRedisKey(Integer userId, String type) {
        return (USER_REDIS_KEY_PIX + type + userId).toUpperCase();
    }

    private static String getUserTokenRedisKey(Integer userId, String type) {
        return (USER_TOKEN_REDIS_KEY_PIX + type + userId).toUpperCase();
    }
}
