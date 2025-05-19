package com.iecube.community.model.Ai2830.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.Ai2830.dto.AI2830Msg;
import com.iecube.community.model.Ai2830.dto.StreamAi2830Msg;
import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Component
public class ConnectToAi2830 implements Runnable {

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

    private WebSocketSession session;

    private final BlockingQueue<String> frontNewAi2830ConnectChatId;
    private final ConcurrentHashMap<String, WebSocketSession> Ai2830ChatIdToSession;
//    private final ConcurrentHashMap<String, String> SessionIdToAi2830ChatId;
    private final ConcurrentHashMap<String, Socket> Ai2830ChatIdToSocket;
    private final ConcurrentHashMap<String, String> SocketIdToChatId;
    private final StringBuilder responseBuffer = new StringBuilder();
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ConnectToAi2830(
            ConcurrentHashMap<String, WebSocketSession> Ai2830ChatIdToSession,
            ConcurrentHashMap<String, String> SessionIdToAi2830ChatId,
            ConcurrentHashMap<String, Socket> Ai2830ChatIdToSocket,
            ConcurrentHashMap<String, String> SocketIdToChatId,
            BlockingQueue<String> FrontNewAi2830ConnectChatId ) {
        this.Ai2830ChatIdToSession = Ai2830ChatIdToSession;
//        this.SessionIdToAi2830ChatId = SessionIdToAi2830ChatId;
        this.Ai2830ChatIdToSocket = Ai2830ChatIdToSocket;
        this.SocketIdToChatId = SocketIdToChatId;
        log.info("ConnectToAi2830 --> start");

        this.frontNewAi2830ConnectChatId = FrontNewAi2830ConnectChatId;
    }

    @Override
    public void run() {
        log.info("Ai2830-connectService-->running");
        //使用异步IO 处理webSocket 连接
        while(true){
            try{
                String chatId = frontNewAi2830ConnectChatId.take();
                log.info("there are {} in (2830sokIo)SocketIdToChatId, {} in Ai2830ChatIdToSocket",SocketIdToChatId.size(),Ai2830ChatIdToSocket.size());
                log.info("try new Ai2830 SocketIo:{}", chatId);
                this.session = Ai2830ChatIdToSession.get(chatId);  // 对应的前端websocket
                socketIoConnect(chatId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void socketIoConnect(String chatId) {
        try {
            if (isConnected()) {
                return;
            }
            // 创建 Socket 实例并配置
            IO.Options options = new IO.Options();
            options.reconnection = false; // 禁用内置重连，使用自定义逻辑
            options.transports = new String[]{"websocket", "polling"};
            socket = IO.socket(new URI(serverUrl+"/server_api?user_id="+chatId), options);
            // 注册核心事件
            registerCoreEventListeners(chatId);
            // 启动连接
            log.info("连接2830AI服务：{}", serverUrl+"/server_api?user_id="+chatId);
            socket.connect();
        } catch (Exception e) {
            log.error("连接到2830AI服务失败: {}" , e.getMessage());
        }
    }

    public void registerCoreEventListeners(String chatId) {
        socket.on(Socket.EVENT_CONNECT, args -> { // 连接成功
            log.info("已连接到2830AI服务");
            currentAttempts = 0;
            this.Ai2830ChatIdToSocket.put(chatId, socket);  // chatId 对应的socketIo
            this.SocketIdToChatId.put(socket.id(), chatId); // socket 对应的 chatId
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {// 连接错误
            log.error("连接2830AI服务错误: {}" , args[0]);
            this.Ai2830ChatIdToSocket.remove(chatId);
            this.SocketIdToChatId.remove(socket.id());
            try {
                session.close(CloseStatus.SERVER_ERROR);
            }catch (IOException e){
                log.info("连接2830AI服务错误,断开前端websocket异常：{}",e.getMessage());
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, args -> {  // 断开连接
            log.info("AI2830服务连接已断开");
            this.Ai2830ChatIdToSocket.remove(chatId);
            if ( socket.id()!=null && this.SocketIdToChatId.contains(socket.id())) {
                this.SocketIdToChatId.remove(socket.id());
            }
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException e) {
                log.info("AI2830服务连接已断开,断开前端websocket异常：{}",e.getMessage());
            }
        });

        socket.on("stream_start",args->{
            responseBuffer.setLength(0); // 清空缓冲区
            StreamAi2830Msg streamAi2830Msg = new StreamAi2830Msg();
            streamAi2830Msg.setType("activity-start");
            try {
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(streamAi2830Msg)));
            } catch (IOException e) {
                log.error("转发AI2830消息json格式化异常：activity-start {}", e.getMessage());
            }
        });

        socket.on("stream_token",args->{
            if (args.length > 0 && args[0] instanceof JSONObject) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    String token = data.getString("token");
                    responseBuffer.append(token);
                    // System.out.print(token); // 实时输出token
                    StreamAi2830Msg streamAi2830Msg = new StreamAi2830Msg();
                    AI2830Msg msg = new AI2830Msg();
                    msg.setMessage(token);
                    streamAi2830Msg.setMessage(msg);
                    streamAi2830Msg.setType("stream");
                    session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(streamAi2830Msg)));
                } catch (JSONException e) {
                    log.error("解析来自2830的消息异常：stream_token: {}", e.getMessage());
                } catch (IOException e) {
                    log.error("转发AI2830消息json格式化异常: stream : {}", e.getMessage());
                }
            }
        });
        socket.on("stream_end",args->{
            StreamAi2830Msg streamAi2830Msg = new StreamAi2830Msg(); // message 消息
            AI2830Msg msg = new AI2830Msg();
            msg.setMessage(responseBuffer.toString());
            msg.setDirection("ai");
            msg.setTimestamp(LocalDateTime.now().format(formatter));
            streamAi2830Msg.setMessage(msg);
            streamAi2830Msg.setType("message");
            StreamAi2830Msg stopMsg = new StreamAi2830Msg(); // activity-stop 消息
            stopMsg.setType("activity-stop");
            try {
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(streamAi2830Msg)));
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(stopMsg)));
            } catch (IOException e) {
                log.error("转发AI2830消息json格式化异常: message stop : {}", e.getMessage());
            }
        });
        socket.on("error",args->{
            if (args.length > 0) {
                try{
                    JSONObject data = (JSONObject) args[0];
                    String message = data.getString("message");
                    StreamAi2830Msg streamAi2830Msg = new StreamAi2830Msg();
                    AI2830Msg msg = new AI2830Msg();
                    msg.setMessage(message);
                    streamAi2830Msg.setType("error");
                    streamAi2830Msg.setMessage(msg);
                    session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(streamAi2830Msg)));

                }catch (JSONException | JsonProcessingException e){
                    log.error("处理AI2830错误消息异常");
                } catch (IOException e) {
                    log.error("转发AI2830消息异常: message error : {}", e.getMessage());
                }

                if (args[0].toString().contains("Connection rejected")) {
                    log.error("AI2830 Connection rejected");
                }
            }
        });
    }

    public boolean isConnected() {
        return socket != null && socket.connected();
    }
}
