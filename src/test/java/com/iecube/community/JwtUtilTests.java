package com.iecube.community;

import com.iecube.community.util.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class JwtUtilTests {
    @Resource
    private JwtUtil jwtUtil;

    @Test
    public void createTest(){
        Map<String, Object> claims =new HashMap<>();
        claims.put("id",1);
        claims.put("name", "kongzi");
        claims.put("user_type", "teacher");
        System.out.println(jwtUtil.createToken(claims));
    }

    @Test
    public void getUserId(){
        String token= "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpZWN1YmUub25saW5lIiwidXNlcl90eXBlIjoidGVhY2hlciIsIm5hbWUiOiJrb25nemkiLCJpZCI6MSwiZXhwIjoxNzI3MTAyNDEzLCJpYXQiOjE3MjcwOTE2MTN9.zU7Ehf1c0R33y9DG_53L9a1XGo1GlYOgFHcaC_hJtqM";
        Claims claims = jwtUtil.getClaims(token);
        Integer userId = (Integer) claims.get("id");
        String userType = (String) claims.get("user_type");
        System.out.println(userId);
        System.out.println(userType);
    }

    @Test
    public void validToken(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpZWN1YmUub25saW5lIiwidXNlcl90eXBlIjoidGVhY2hlciIsIm5hbWUiOiJrb25nemkiLCJpZCI6MSwiZXhwIjoxNzI3MTAyNDEzLCJpYXQiOjE3MjcwOTE2MTN9.zU7Ehf1c0R33y9DG_53L9a1XGo1GlYOgFHcaC_hJtqM";
        System.out.println(jwtUtil.validateToken(token));
        System.out.println(jwtUtil.validateToken(token));

    }
}
