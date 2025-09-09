package com.iecube.community.model.AI.aiClient.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.AI.aiClient.AiClientWebSocketHandler;
import com.iecube.community.model.AI.aiClient.dto.MarkerQuestion;
import com.iecube.community.model.AI.aiClient.service.AiApiService;
import com.iecube.community.model.AI.aiMiddlware.WebSocketSessionManage;
import com.iecube.community.model.AI.dto.AiCheckResult;
import com.iecube.community.model.AI.ex.AiAPiResponseException;
import com.iecube.community.model.AI.mapper.AiCheckResultMapper;
import com.iecube.community.util.xlsx.CheckHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AiApiServiceImpl implements AiApiService {

    @Value("${Ai.baseUrl}")
    private String baseUrl;

    @Value("${Ai.wssBaseUrl}")
    private String wssBaseUrl;

    @Value("${Ai.header.auth.field}")
    private String headerFiled;

    @Value("${Ai.header.auth.val}")
    private String headerVal;

    @Value("${Ai.model.procedure}")
    private String modelProcedure;

    @Value("${Ai.model.llm}")
    private String modelLlm;

    @Value("${Ai.model.llm_short}")
    private String modelLlmShort;

    @Value("${Ai.module.name}")
    private String moduleName;

    @Autowired
    private WebSocketSessionManage webSocketSessionManage;

    @Autowired
    private AiCheckResultMapper aiCheckResultMapper;

    @Override
    public String genChat() {
        String uri;
        if(modelProcedure.equals("default")){
            uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/interact/chat")
                    .queryParam("procedure", modelProcedure)
                    .toUriString();
        }else {
            uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/interact/chat")
                    .queryParam("procedure", modelProcedure)
                    .queryParam("llm", modelLlm)
                    .queryParam("llm_short", modelLlmShort)
                    .toUriString();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");
        headers.add(headerFiled, headerVal);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
            CheckHttpResponse.CheckResult checkResult = new CheckHttpResponse().responseNormal(response);
            if(checkResult.isNormal()){
                return checkResult.getBodyData().get("chat_id").asText();
            }else {
                throw new AiAPiResponseException("访问AI资源失败："+checkResult.getErrorReason());
            }
        }catch (Exception e){
            throw new AiAPiResponseException("访问AI资源失败："+e.getMessage());
        }

    }

    @Override
    public void webSocketConnect(String chatId) {
//        String url = wssBaseUrl+chatId;
//        WebSocketClient client = new StandardWebSocketClient();
//        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
//        headers.add(headerFiled, headerVal);
//        if(webSocketSessionManage.clientSessionManager.getSessionById(chatId)!=null){
//            try{
//                webSocketSessionManage.clientSessionManager.getSessionById(chatId).close(CloseStatus.NORMAL);
//            }catch (Exception e){
//                log.warn("连接到ai服务前-->新建连接前关闭已有连接, {}",e.getMessage());
//            }
//            webSocketSessionManage.clientSessionManager.removeSession(chatId);
//        }
//        try {
//            URI uri = new URI(url);
//            WebSocketSession session = client.doHandshake(webSocketHandler,headers, uri).get();
//            session.setTextMessageSizeLimit(10485760);
//            webSocketSessionManage.clientSessionManager.addSession(chatId, session);
//        } catch (Exception e) {
//            throw new AiAPiResponseException("与AI服务建立消息通道错误："+e.getMessage());
//        }
        log.info("websocket-to-w6");
    }

    @Override
    public void webSocketDisConnect(String chatId) {
//        WebSocketSession session = webSocketSessionManage.clientSessionManager.getSessionById(chatId);
//        if(session != null){
//            if(session.isOpen()){
//                try{
//                    session.close(new CloseStatus(CloseStatus.NORMAL.getCode()));
//                }
//                catch (Exception e){
//                    throw new AiAPiResponseException("关闭对话连接异常");
//                }
//            }
//        }
        log.info("websocket-disconnect-w6");
        return;
    }

    // 助教
    @Override
    public void useTeachingAssistant(String chatId, String bookId, String sectionPrefix, List<String> imgList, String question){
        String uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/interact/agent").toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");
        headers.add(headerFiled, headerVal);
        // 构建请求体对象
        Map<String, Object> requestBodyMap = new HashMap<>();
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("book_id", bookId);
        payloadMap.put("image_dataurls", imgList);
        payloadMap.put("section_prefix", sectionPrefix);
        payloadMap.put("question", question);
        requestBodyMap.put("payload", payloadMap);
        requestBodyMap.put("chat_id", chatId);
        requestBodyMap.put("module_name", moduleName);
        requestBodyMap.put("module_source", null);
        requestBodyMap.put("agent_name", "teaching_assistant");
        requestBodyMap.put("llm_model_override", modelLlm);
        RestTemplate restTemplate = new RestTemplate();
        // 创建 ObjectMapper 实例用于将 Java 对象转换为 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            // 将请求体对象转换为 JSON 字符串
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);
//            System.out.println(requestBody);
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody,headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
            CheckHttpResponse.CheckResult checkResult = new CheckHttpResponse().responseNormal(response);
            if(checkResult.isNormal()){
                log.info("助教(teaching_assistant):{},{}",chatId,question);
            }else {
                log.warn("助教(teaching_assistant):{};{};{};{}",chatId,sectionPrefix,imgList,question);
                throw new AiAPiResponseException("访问AI资源失败(响应错误)："+checkResult.getErrorReason());
            }
        }catch (Exception e){
            log.error("助教(teaching_assistant):{};{};{};{}",chatId,sectionPrefix,imgList,question);
            throw new AiAPiResponseException("访问AI资源失败："+e.getMessage());
        }
    }

    // 出题
    public void useQuestioner(String chatId, String bookId, Integer qAmount, String scene, String sectionPrefix){
        String uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/interact/agent").toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");
        headers.add(headerFiled, headerVal);
        // 构建请求体对象
        Map<String, Object> requestBodyMap = new HashMap<>();
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("book_id", bookId);
        payloadMap.put("n_questions", qAmount);
        payloadMap.put("scene", scene);
        payloadMap.put("section_prefix", sectionPrefix);
        requestBodyMap.put("payload", payloadMap);
        requestBodyMap.put("agent_name", "questioner");
        requestBodyMap.put("chat_id", chatId);
        requestBodyMap.put("llm_model_override", modelLlm);
        requestBodyMap.put("module_name", moduleName);
        requestBodyMap.put("module_source", null);
        RestTemplate restTemplate = new RestTemplate();
        // 创建 ObjectMapper 实例用于将 Java 对象转换为 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            // 将请求体对象转换为 JSON 字符串
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);
//            System.out.println(requestBody);
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody,headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
            CheckHttpResponse.CheckResult checkResult = new CheckHttpResponse().responseNormal(response);
            if(checkResult.isNormal()){
                log.info("提问(questioner):{},{}",chatId,scene);
            }else {
                log.warn("提问(questioner):{};{};{}",chatId,sectionPrefix,scene);
                throw new AiAPiResponseException("访问AI资源失败(响应错误)："+checkResult.getErrorReason());
            }
        }catch (Exception e){
            log.error("提问(questioner):{};{};{}",chatId,sectionPrefix,scene);
            throw new AiAPiResponseException("访问AI资源失败："+e.getMessage());
        }
    }


    // 校验
    public void useMarker(String chatId, String bookId, MarkerQuestion question, String stuAnswer, String sectionPrefix, List<String> imgList){
        String uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/interact/agent").toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");
        headers.add(headerFiled, headerVal);
        // 构建请求体对象
        Map<String, Object> requestBodyMap = new HashMap<>();
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("book_id", bookId);
        payloadMap.put("image_dataurls", imgList);
        payloadMap.put("question", question);
        payloadMap.put("section_prefix", sectionPrefix);
        payloadMap.put("student_answer", stuAnswer);
        requestBodyMap.put("payload", payloadMap);
        requestBodyMap.put("agent_name", "marker");
        requestBodyMap.put("chat_id", chatId);
        requestBodyMap.put("llm_model_override", "deepseek-r1");
        requestBodyMap.put("module_name", moduleName);
        requestBodyMap.put("module_source", null);
        RestTemplate restTemplate = new RestTemplate();
        // 创建 ObjectMapper 实例用于将 Java 对象转换为 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            // 将请求体对象转换为 JSON 字符串
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);
//            System.out.println(requestBody);
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody,headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
            CheckHttpResponse.CheckResult checkResult = new CheckHttpResponse().responseNormal(response);
            if(checkResult.isNormal()){
                log.info("校验(marker):{},{},{}",chatId, sectionPrefix, question.getQuestion());
            }else {
                log.warn("校验(marker):{};{};{}",chatId,sectionPrefix,question.getQuestion());
                throw new AiAPiResponseException("访问AI资源失败(响应错误)："+checkResult.getErrorReason());
            }
        }catch (Exception e){
            log.error("校验(marker):{};{};{}",chatId,sectionPrefix,question.getQuestion());
            throw new AiAPiResponseException("访问AI资源失败："+e.getMessage());  // todo 这里会抛出502异常 访问AI资源失败：502 Bad Gateway: [no body]
//            java.lang.NullPointerException: Cannot invoke "java.lang.Throwable.getMessage()" because the return value of "java.lang.Throwable.getCause()" is null
//            at com.iecube.community.basecontroller.BaseController.handleException(BaseController.java:49) ~[classes!/:0.0.1-SNAPSHOT]
        }
    }

    // json内容
    @Override
    public JsonNode getJsonRes(String artefactId, Integer studentId, Long taskId, String type) {
        String uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/interact/artefact/"+artefactId)
                    .queryParam("include_content", true)
                    .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");
        headers.add(headerFiled, headerVal);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
            CheckHttpResponse.CheckResult checkResult = new CheckHttpResponse().responseNormal(response);
            if(checkResult.isNormal()){
                log.info("获取ai message json格式 {}", artefactId);
                JsonNode res = checkResult.getBodyData();
                if(type.equals("marker")){
                    AiCheckResult aiCheckResult = new AiCheckResult();
                    aiCheckResult.setStudentId(studentId);
                    aiCheckResult.setTaskId(taskId);
                    aiCheckResult.setResult(new ObjectMapper().writeValueAsString(res));
                    aiCheckResultMapper.insert(aiCheckResult);
                }
                return res;
            }else {
                log.warn("获取ai message json格式 {}", artefactId);
                throw new AiAPiResponseException("访问AI资源失败："+checkResult.getErrorReason());
            }
        }catch (Exception e){
            log.error("获取ai message json格式 {}", artefactId);
            throw new AiAPiResponseException("访问AI资源失败："+e.getMessage());
        }
    }
}
