package com.iecube.community.model.elaborate_md_task.check;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.WebSocketMessage;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WaitingCheckWebSocketConsumer implements Runnable{
    @Value("${Ai.wssBaseUrl}")
    private String wssBaseUrl;

    @Value("${Ai.header.auth.field}")
    private String headerFiled;

    @Value("${Ai.header.auth.val}")
    private String headerVal;

    private final BlockingQueue<String> chatIdQueue; //chatId 队列 缓冲
    private final ConcurrentHashMap<String, Check> waitingChecks;

    @Autowired
    public WaitingCheckWebSocketConsumer(BlockingQueue<String> chatIdQueue, ConcurrentHashMap<String, Check> waitingCheck) {
        this.chatIdQueue = chatIdQueue;
        this.waitingChecks = waitingCheck;
        log.info("check--> WaitingCheckWebSocketConsumer start");
    }

    @Override
    public void run() {
        log.info("WaitingCheckWebSocketConsumer--> WaitingCheckWebSocketConsumer running");
        while (true) {
            try {
                String chatId = chatIdQueue.take();
                processWebSocket(chatId);  //todo 测试并发
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while taking chatId from queue: {}", e.getMessage(), e);
            }
        }
    }

    private void processWebSocket(String chatId) {
        log.info("WaitingCheckWebSocketConsumer-->  {}, {}", chatId, waitingChecks.get(chatId));
        ObjectMapper objectMapper = new ObjectMapper();
        WebSocketClient client = new ReactorNettyWebSocketClient();
        HttpHeaders headers = new HttpHeaders();
        headers.add(headerFiled, headerVal);
        client.execute(URI.create(wssBaseUrl + chatId), headers, session -> {
            Check waitingCheck = waitingChecks.get(chatId);
            return session.receive().map(WebSocketMessage::getPayloadAsText)
                    .doOnNext(message -> {
                        // 处理接收到的消息
                        log.info("check--> 收到消息：{}",message);
//                        try{
//                            JsonNode msg = objectMapper.readTree(message);
//                            log.info(msg.toString());
//                            if(Objects.equals(msg.get("type").asText(), "message-ack")){
//                                String checkResId = msg.get("payload").get("artefacts").get(0).get("id").asText();
//                                log.info("check --> result id {}", checkResId);
//                            }
//                        } catch (JsonProcessingException e) {
//                            log.error("解析json错误:{}",e.getMessage());
//                        }
                    }).then();
        }).block();
    }
}
