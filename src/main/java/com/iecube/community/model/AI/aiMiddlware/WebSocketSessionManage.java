package com.iecube.community.model.AI.aiMiddlware;

import com.iecube.community.model.AI.consumer.CloseConnectOfW6;
import com.iecube.community.model.AI.consumer.ConnectToW6;
import com.iecube.community.util.SessionManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


@Configuration
public class WebSocketSessionManage {
    /**
     * chatId --> clientSession
     * clientSession --> chatId
     * 以上为 和 AI 方连接的管理
     * ------------------------------
     * 以下为服务端的链接管理
     * chatId --> serviceSession
     * serviceSession -- chatId
     * 需求：
     * 需要在 onTextMessage 时 能够知道当前的chatId 是什么
     * 需要在知道chatId的时候 知道给那个session发送消息
     * */
//    public final SessionManager clientSessionManager = new SessionManager();
//    public final SessionManager serverSessionManager = new SessionManager();

    @Bean
    public ConcurrentHashMap<String, WebSocketSession> W6ChatIdToSession(){
        return new ConcurrentHashMap<>();
    }
    @Bean
    public ConcurrentHashMap<String, String> W6SessionIdToChatId(){
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, WebSocketSession> FrontChatIdToSession(){
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, String> FrontSessionIdToChatId(){
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, WebSocketSession> FrontSessionIdToW6Session(){
        return new ConcurrentHashMap<>();
    }

    /**
     * 前端建立了websocket连接， 记录当前的chatId，放到队列中，由另外线程连接到w6
     * */
    @Bean
    public BlockingQueue<String> FrontNewConnectChatId(){
        return new LinkedBlockingQueue<>();
    }


    @Bean
    public BlockingQueue<WebSocketSession> FrontClosedSession(){
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public CommandLineRunner ConnectToW6Consumer(ConnectToW6 connectToW6) {
        return args -> {
            Thread thread =  new Thread(connectToW6);
            thread.setName("w6-connect");
            thread.start();
        };
    }

    @Bean
    public CommandLineRunner CloseConnectOfW6Consumer(CloseConnectOfW6 closeConnectOfW6) {
        return args -> {
            Thread thread =  new Thread(closeConnectOfW6);
            thread.setName("XX-w6-connect");
            thread.start();
        };
    }

}
