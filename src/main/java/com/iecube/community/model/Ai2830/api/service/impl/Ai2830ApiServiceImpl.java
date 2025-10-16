package com.iecube.community.model.Ai2830.api.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.AI.ex.AiAPiResponseException;
import com.iecube.community.model.Ai2830.api.service.Ai2830ApiService;
import com.iecube.community.util.xlsx.CheckHttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class Ai2830ApiServiceImpl implements Ai2830ApiService {

    @Value("${AI2830.server.url}")
    private String baseUrl;

    @Value("${AI2830.server.key}")
    private String KEY;

    @Override
    public String register() {
        String uri= UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/server_chat/register")
                    .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");

        // 构建请求体对象
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("key",KEY);
        requestBodyMap.put("course_id","2830");
        requestBodyMap.put("teacher_type","assistant");
        RestTemplate restTemplate = new RestTemplate();
        // 创建 ObjectMapper 实例用于将 Java 对象转换为 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody,headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
            CheckHttpResponse.CheckResult checkResult = new CheckHttpResponse().responseNormal(response);
            if(checkResult.isNormal()){
                return checkResult.getBodyData().get("data").get("user_id").asText();
            }else {
                throw new AiAPiResponseException("访问AI2830资源失败："+checkResult.getErrorReason());
            }
        }catch (Exception e){
            throw new AiAPiResponseException("访问AI2830资源失败："+e.getMessage());
        }
    }

    @Override
    public JsonNode getUserIdMessages(String userId, String courseId, Integer page) {
        String uri= UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/server_chat/messages")
                .queryParam("user_id",userId)
                .queryParam("course_id",courseId)
                .queryParam("page",page)
                .queryParam("per_page", 100)
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");
        try{
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
            CheckHttpResponse.CheckResult checkResult = new CheckHttpResponse().responseNormal(response);
            if (checkResult.isNormal()){
                return checkResult.getBodyData().get("data");
            }else {
                throw new AiAPiResponseException("访问AI2830资源失败："+checkResult.getErrorReason());
            }
        }catch (Exception e){
            throw new AiAPiResponseException("访问AI2830资源失败："+e.getMessage());
        }
    }
}
