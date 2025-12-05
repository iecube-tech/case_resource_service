package com.iecube.community.model.EMDV4Project.EMDV4Analysis.aiAgent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.AI.ex.AiAPiResponseException;
import com.iecube.community.util.xlsx.CheckHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EvaluationAgent {
    @Value("${AIEvaluation.baseUrl}")
    private String baseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Async("evaluationExecutor")
    public CompletableFuture<String> evaluate(String details, String type) {
        log.debug(type);
        String uri  = UriComponentsBuilder.fromHttpUrl(baseUrl+"/evaluation/generate").toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");
        // 构建请求体对象
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("experiment_details", details);
        requestBodyMap.put("evaluation_type", type);
        RestTemplate restTemplate = new RestTemplate();
        try{
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody,headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
            CheckHttpResponse.CheckResult checkResult = new CheckHttpResponse().responseNormal(response);
            if(checkResult.isNormal()){
                log.info("[{}]评价agent生成成功",type);
                return CompletableFuture.completedFuture(checkResult.getBodyData().get("content").asText());
            }else {
                log.warn("[{}]评价agent生成失败",type);
                throw new AiAPiResponseException("访问评价agent败(响应错误)："+checkResult.getErrorReason());

            }
        }catch (JsonProcessingException | AiAPiResponseException e) {
            throw new RuntimeException(e);
        }
    }
}
