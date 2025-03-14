package com.iecube.community.util.WebSocket;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

public class WebSocketClientExample {

    public static void main(String[] args) {
        // 创建 WebSocket 客户端
        StandardWebSocketClient client = new StandardWebSocketClient();

        // 创建自定义的 WebSocketHandler
        WebSocketHandler handler = new WebSocketHandler() {
            @Override
            public void handleMessage(WebSocketSession session, org.springframework.web.socket.WebSocketMessage<?> message) {
                // 接收到服务器消息时的处理逻辑
                System.out.println("Received message: " + message.getPayload());
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

            }

            @Override
            public boolean supportsPartialMessages() {
                return false;
            }

            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                // 连接建立后的处理逻辑
                System.out.println("Connected to WebSocket Server");
                session.sendMessage(new TextMessage("Hello WebSocket Server!"));
            }
        };

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("x-w6-api-key","5byg5bCP6b6ZOjp4aWFvbG9uZzo6VlhjbmQzQ25BWEttbTVjMU8wSUw2RXMtajRkRm5FWUUwTDRHX1RPbkpNMD0=");
        // 创建 WebSocket 连接管理器
        WebSocketConnectionManager manager = new WebSocketConnectionManager(client, handler, "wss://beta.megamoyo.com/api/interact/chat/5Q3LJGphRSPKeIdldHq3Le");
        manager.setHeaders(headers);
        manager.start(); // 启动连接

        // 等待 WebSocket 客户端连接和消息交换
        try {
            Thread.sleep(5000);  // 等待 5 秒钟进行通信
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
