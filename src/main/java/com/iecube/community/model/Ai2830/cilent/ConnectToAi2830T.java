package com.iecube.community.model.Ai2830.cilent;

import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ConnectToAi2830T {

    @Getter
    private Socket socket;
    private ScheduledExecutorService reconnectExecutor;

    @Value("${AI2830.server.url}")
    private String serverUrl;

    @Value("${AI2830.reconnect.enabled}")
    private boolean reconnectEnabled;

    @Value("${AI2830.reconnect.delay}")
    private int reconnectDelay;

    @Value("${AI2830.reconnect.max-attempts}")
    private int maxReconnectAttempts;

    private int currentAttempts = 0;

    @PostConstruct
    public void init() throws URISyntaxException {
        reconnectExecutor = Executors.newSingleThreadScheduledExecutor();
        connect();
    }

    private void connect() {
        try {
            if (isConnected()) {
                return;
            }
            String encodedUserId = URLEncoder.encode("ZPaVpKoAhcLbSpCD", StandardCharsets.UTF_8);
            String urlWithParams = String.format("%s?user_id=%s", serverUrl, encodedUserId);
            // 创建 Socket 实例并配置
            IO.Options options = new IO.Options();
            options.reconnection = false; // 禁用内置重连，使用自定义逻辑
            System.out.println(new URI(urlWithParams));
            socket = IO.socket(new URI(urlWithParams), options);

            // 注册核心事件
            registerCoreEventListeners();

            // 启动连接
            System.out.println(socket.connect());
            currentAttempts = 0;
            log.info("连接2830AI服务：{}", serverUrl);

        } catch (Exception e) {
            log.error("连接到2830AI服务失败: {}" , e.getMessage());
            scheduleReconnect();
        }
    }

    public void registerCoreEventListeners() {
        // 连接成功
        socket.on(Socket.EVENT_CONNECT, args -> {
            log.info("连接到2830AI服务");
            currentAttempts = 0;
        });

        // 连接错误
        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            log.error("连接2830AI服务错误: {}" , args[0]);
            scheduleReconnect();
        });

        // 断开连接
        socket.on(Socket.EVENT_DISCONNECT, args -> {
            log.warn("2830AI服务断开连接");
            if (reconnectEnabled) {
                scheduleReconnect();
            }
        });

        socket.on("message", args -> {
            System.out.println("收到消息");
            System.out.println(args);
        });

    }

    private void scheduleReconnect() {
        if (!reconnectEnabled) return;

        if (currentAttempts >= maxReconnectAttempts) {
            System.err.println("2830AI服务达到最大重连次数");
            return;
        }

        currentAttempts++;
        long delay = (long) (reconnectDelay * Math.pow(1.5, currentAttempts - 1)); // 指数退避策略

        log.info("2830AI服务重连Scheduling reconnect attempt {} in {} ms", currentAttempts, delay);

        reconnectExecutor.schedule(this::connect, delay, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void cleanup() {
        if (reconnectExecutor != null) {
            reconnectExecutor.shutdownNow();
        }

        if (socket != null) {
            socket.off(); // 移除所有监听器
            socket.disconnect();
        }
    }

    public boolean isConnected() {
        return socket != null && socket.connected();
    }

    // 发送消息到服务器
    public void sendMessage(String userId, String content, String courseId, String teacherType) {
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("user_id", userId);
        requestBodyMap.put("content", content);
        requestBodyMap.put("course_id", courseId);
        requestBodyMap.put("teacher_type", teacherType);
        if (isConnected()) {
//            socket.emit("send_message",requestBodyMap);
            socket.send(requestBodyMap);
            log.info("向AI2830服务发送消息{}",requestBodyMap);
        } else {
            log.error("向AI2830服务发送消息失败：Socket未连接");
        }
    }
}