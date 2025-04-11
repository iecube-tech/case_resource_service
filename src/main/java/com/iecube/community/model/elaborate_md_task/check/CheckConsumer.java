package com.iecube.community.model.elaborate_md_task.check;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.AI.aiClient.dto.MarkerQuestion;
import com.iecube.community.model.AI.ex.AiAPiResponseException;
import com.iecube.community.model.elaborate_md_task.check.websocket.CheckWebSocketHandler;
import com.iecube.community.util.xlsx.CheckHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CheckConsumer implements Runnable {
    @Value("${Ai.baseUrl}")
    private String baseUrl;

    @Value("${Ai.wssBaseUrl}")
    private String wssBaseUrl;

    @Value("${Ai.model.procedure}")
    private String modelProcedure;

    @Value("${Ai.header.auth.field}")
    private String headerFiled;

    @Value("${Ai.header.auth.val}")
    private String headerVal;

    @Value("${Ai.module.name}")
    private String moduleName;

    static final String bookId="5S8NzabuYiO0KgMn1QtXKL";


    private final BlockingQueue<Check> taskQueue;
    private final BlockingQueue<String> chatIdQueue; //chatId 队列 缓冲
    private final ConcurrentHashMap<String, Check> waitingCheck;

//    @Autowired
//    private CheckConfig checkConfig;

//    @Autowired
//    private WaitingCheckWebSocketConsumer waitingCheckWebSocketConsumer;

    @Autowired
    public CheckConsumer(BlockingQueue<Check> taskQueue, BlockingQueue<String> chatIdQueue, ConcurrentHashMap<String, Check> waitingCheck ) {
        log.info("check--> CheckConsumer start");
        this.taskQueue = taskQueue;
        this.chatIdQueue = chatIdQueue;
        this.waitingCheck = waitingCheck;
        log.info("CheckConsumer taskQueue ==> {}", System.identityHashCode(taskQueue));
        log.info("CheckConsumer chatIdQueue ==> {}", System.identityHashCode(chatIdQueue));
        log.info("CheckConsumer waitingCheck ==> {}", System.identityHashCode(waitingCheck));
    }

    @Override
    public void run() {
        log.info("check--> CheckConsumer running");
        while (true) {
            try{
                Check needcheck = taskQueue.take();
                progressCheck(needcheck);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while taking task from queue: {}", e.getMessage(), e);
            }
        }
    }

    private void progressCheck(Check needcheck) {
        log.info("progressCheck");
        // 获取 chatId  //每个校验都用新的chat 一个校验请求一个对话   //todo mapper->  taskId -- checkChatId
        // 建立 ai 对话
        //  发送 agent 请求 生产者-> waitingCheckFinish
            // ai对话 处理接收到的消息 消费者  --> 清理 waitingCheckFinish   收到消息， 获取json格式的数据 保存json格式的数据？ 保存结果
        try{
            needcheck.getQuestion().setId(needcheck.getCellId());
            String chatId = getChat();
            log.info("getChat {}", chatId);
            chatIdQueue.put(chatId);
            waitingCheck.put(chatId, needcheck);
            useMarker(chatId, bookId,needcheck.getQuestion(),needcheck.getCellStuAnswer(), needcheck.getSectionPrefix(), needcheck.getCellStuImgList());
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String getChat(){
        String uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/interact/chat")
                    .queryParam("procedure", modelProcedure)
                    .queryParam("llm", "deepseek-r1")
                    .queryParam("llm_short", "DS-R1")
                    .toUriString();

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

    private void webSocketConnect(String chatId) {
        String url = wssBaseUrl+chatId;
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add(headerFiled, headerVal);
        try {
            URI uri = new URI(url);
            WebSocketSession session = client.doHandshake(new CheckWebSocketHandler() ,headers, uri).get();
            session.setTextMessageSizeLimit(10485760);
        } catch (Exception e) {
            throw new AiAPiResponseException("与AI服务建立消息通道错误："+e.getMessage());
        }
    }

    private void useMarker(String chatId, String bookId, MarkerQuestion question, String stuAnswer, String sectionPrefix, List<String> imgList){
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
                log.info("check-->校验(marker):{},{},{}",chatId, sectionPrefix, question.getQuestion());
            }else {
                log.warn("check-->校验(marker):{};{};{}",chatId,sectionPrefix,question.getQuestion());
                throw new AiAPiResponseException("访问AI资源失败(响应错误)："+checkResult.getErrorReason());
            }
        }catch (Exception e){
            log.error("check-->校验(marker):{};{};{}",chatId,sectionPrefix,question.getQuestion());
            throw new AiAPiResponseException("访问AI资源失败："+e.getMessage());
        }
    }
}
