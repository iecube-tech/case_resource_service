package com.iecube.community;

import com.iecube.community.util.JwtUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JwtUtilTests {
    @Resource
    private JwtUtil jwtUtil;

    @Test
    public void createTest(){
        System.out.println(jwtUtil.createToken("kongzi@iecube.com.cn", null));
    }

    @Test
    public void getUserId(){
        System.out.println(jwtUtil.getUserId("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrb25nemlAaWVjdWJlLmNvbS5jbiIsImlhdCI6MTY5MTQ5MjQwNCwiZXhwIjoxNjkxNDk2MDA0fQ.1GOELG9kk3yYAGogFQRMZ6mvbJOD-N7KHNPeoURjMNI"));
    }

    @Test
    public void validToken(){
        System.out.println(jwtUtil.validateToken("dfaaa"));
        System.out.println(jwtUtil.validateToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrb25nemlAaWVjdWJlLmNvbS5jbiIsImlhdCI6MTY5MTQ5MjQwNCwiZXhwIjoxNjkxNDk2MDA0fQ.1GOELG9kk3yYAGogFQRMZ6mvbJOD-N7KHNPeoURjMNI"));
    }
}
