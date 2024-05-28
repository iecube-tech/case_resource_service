package com.iecube.community.model.websocket.client;

import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

public class WebSocketClientManager {

    public StompSession connect(String url) throws ExecutionException, InterruptedException {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {};

        return stompClient.connect(url, sessionHandler).get();
    }

    // 发送消息
    public void sendMessage(StompSession session, String destination, String message) {
        session.send(destination, message.getBytes());
    }

    // 接收消息
    public void subscribeToDestination(StompSession session, String destination) {
        session.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return byte[].class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // 处理接收到的消息
                byte[] message = (byte[]) payload;
                System.out.println("Received message: " + new String(message));
            }
        });
    }

}
