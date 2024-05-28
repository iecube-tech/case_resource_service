package com.iecube.community.model.device.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class DeviceServiceTests {

    @Test
    public void sendTest(){
        // 创建 RestTemplate 实例
        RestTemplate restTemplate = new RestTemplate();

        // 请求 URL
        String url = "http://127.0.0.1:8000/start";

        // 请求参数
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        Integer a = 48001;
        Integer b = 49001;
        requestBody.add("vnc", a.toString());
        requestBody.add("listen", b.toString());

        // 发送 POST 请求，并接收响应
        String response = restTemplate.postForObject(url, requestBody, String.class);

        // 处理响应
        System.out.println("Response: " + response);
    }

}
