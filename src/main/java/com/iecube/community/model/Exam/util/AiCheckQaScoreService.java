package com.iecube.community.model.Exam.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.AI.ex.AiAPiResponseException;
import com.iecube.community.model.Exam.entity.ExamPaper;
import com.iecube.community.util.xlsx.CheckHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
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
public class AiCheckQaScoreService{

    @Value("${Exam.check.qa.url}")
    private String url;

    @Async
    public CompletableFuture<JsonNode> computeQaScore(ExamPaper examPaper){
        String uri = UriComponentsBuilder.fromHttpUrl(url).toUriString();
        // 构建请求体对象
        Map<String, Object> requestBodyMap = new HashMap<>();
        Map<String, Object> question = new HashMap<>();
        Map<String, Object> answer = new HashMap<>();
        question.put("id", examPaper.getId());
        question.put("type", "QA");
        question.put("question", examPaper.getTitle());
        question.put("answer", examPaper.getResponse());
        question.put("score", examPaper.getScore());
        question.put("hintWhenWrong","");
        question.put("analysis","");

        answer.put("answer", examPaper.getAnswer());
        answer.put("image","");

        requestBodyMap.put("question", question);
        requestBodyMap.put("answer", answer);

        RestTemplate restTemplate = new RestTemplate();
        // 创建 ObjectMapper 实例用于将 Java 对象转换为 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            // 将请求体对象转换为 JSON 字符串
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);
//            System.out.println(requestBody);
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
            CheckHttpResponse.CheckResult checkResult = new CheckHttpResponse().responseNormal(response);
            if(!checkResult.isNormal()){
                throw new AiAPiResponseException("访问AI资源失败(响应错误)："+checkResult.getErrorReason());
            }
            return CompletableFuture.completedFuture(checkResult.getBodyData());
        }catch (Exception e){
            log.error("校验(Exam QA):{};{}",examPaper.getId(),examPaper.getTitle());
            throw new AiAPiResponseException("访问AI资源失败："+e.getMessage());  // todo 这里会抛出502异常 访问AI资源失败：502 Bad Gateway: [no body]
//            java.lang.NullPointerException: Cannot invoke "java.lang.Throwable.getMessage()" because the return value of "java.lang.Throwable.getCause()" is null
//            at com.iecube.community.basecontroller.BaseController.handleException(BaseController.java:49) ~[classes!/:0.0.1-SNAPSHOT]
        }
    }
}
