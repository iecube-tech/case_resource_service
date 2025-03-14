package com.iecube.community.model.AI.aiClient.service.impl;

import com.iecube.community.model.AI.aiClient.AiClientWebSocketHandler;
import com.iecube.community.model.AI.aiClient.service.AiApiService;
import com.iecube.community.model.AI.aiMiddlware.WebSocketSessionManage;
import com.iecube.community.model.AI.ex.AiAPiResponseException;
import com.iecube.community.util.xlsx.CheckHttpResponse;
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

import javax.websocket.*;
import java.net.URI;

@Service
@ClientEndpoint
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

    @Autowired
    private AiClientWebSocketHandler webSocketHandler;

    @Autowired
    private WebSocketSessionManage webSocketSessionManage;

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
        String url = wssBaseUrl+chatId;
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add(headerFiled, headerVal);
        try {
            URI uri = new URI(url);
            WebSocketSession session = client.doHandshake(webSocketHandler,headers, uri).get();
            webSocketSessionManage.clientSessionManager.addSession(chatId, session);
        } catch (Exception e) {
            throw new AiAPiResponseException("与AI服务建立消息通道错误："+e.getMessage());
        }
    }

    @Override
    public void webSocketDisConnect(String chatId) {
        WebSocketSession session = webSocketSessionManage.clientSessionManager.getSessionById(chatId);
        if(session != null){
            if(session.isOpen()){
                try{
                    session.close(new CloseStatus(CloseStatus.NORMAL.getCode()));
                }
                catch (Exception e){
                    throw new AiAPiResponseException("关闭对话连接异常");
                }
            }
        }
    }
}
