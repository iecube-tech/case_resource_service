package com.iecube.community.model.AI.consumer;


import com.iecube.community.model.AI.aiClient.AiClientWebSocketHandler;
import com.iecube.community.model.AI.ex.AiAPiResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ConnectToW6 implements Runnable {

    @Value("${Ai.wssBaseUrl}")
    private String wssBaseUrl;

    @Value("${Ai.header.auth.field}")
    private String headerFiled;

    @Value("${Ai.header.auth.val}")
    private String headerVal;

    @Autowired
    private AiClientWebSocketHandler w6WebSocketHandler;

    private final ConcurrentHashMap<String, WebSocketSession> W6ChatIdToSession;
    private final ConcurrentHashMap<String, String> W6SessionIdToChatId;
    private final ConcurrentHashMap<String, WebSocketSession> FrontChatIdToSession;
    private final ConcurrentHashMap<String, WebSocketSession> FrontSessionIdToW6Session;
    private final BlockingQueue<String> FrontNewConnectChatId;

    public ConnectToW6(ConcurrentHashMap<String, WebSocketSession> W6ChatIdToSession,
                       ConcurrentHashMap<String, String> W6SessionIdToChatId,
                       ConcurrentHashMap<String, WebSocketSession> FrontChatIdToSession,
                       ConcurrentHashMap<String, WebSocketSession> FrontSessionIdToW6Session,
                       BlockingQueue<String> FrontNewConnectChatId){
        log.info("w6-connect-->start");
        this.W6ChatIdToSession = W6ChatIdToSession;
        this.W6SessionIdToChatId = W6SessionIdToChatId;
        this.FrontChatIdToSession = FrontChatIdToSession;
        this.FrontSessionIdToW6Session = FrontSessionIdToW6Session;
        this.FrontNewConnectChatId = FrontNewConnectChatId;
    }

    @Override
    public void run() {
        log.info("w6-connect-->running");
        //使用异步IO 处理webSocket 连接
        while(true){
            try{
                String chatId = FrontNewConnectChatId.take();
                log.info("there are {} in W6SessionIdToChatId, {} in FrontSessionIdToW6Session",W6SessionIdToChatId.size(),FrontSessionIdToW6Session.size());
                log.info("try new w6 webSocket:{}", chatId);
                webSocketConnect(chatId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    public void webSocketConnect(String chatId) {
        String url = wssBaseUrl+chatId;
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add(headerFiled, headerVal);
        try {
            URI uri = new URI(url);
            // new AiClientWebSocketHandler( chatId, W6ChatIdToSession, W6SessionIdToChatId,  FrontChatIdToSession),
            WebSocketSession session = client.doHandshake( w6WebSocketHandler, headers, uri).get();
            session.setTextMessageSizeLimit(10485760);
            W6ChatIdToSession.put(chatId, session);
            W6SessionIdToChatId.put(session.getId(),chatId);
            if(FrontChatIdToSession.get(chatId)!=null){
                this.FrontSessionIdToW6Session.put(FrontChatIdToSession.get(chatId).getId(), session); // 前端 sessionId -> w6 session
            }
        } catch (Exception e) {
            throw new AiAPiResponseException("与AI服务建立消息通道错误："+e.getMessage());
        }
    }
}
