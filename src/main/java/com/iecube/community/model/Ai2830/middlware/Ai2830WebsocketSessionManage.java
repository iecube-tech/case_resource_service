package com.iecube.community.model.Ai2830.middlware;

import com.iecube.community.model.Ai2830.consumer.ConnectToAi2830;
import com.iecube.community.model.Ai2830.consumer.CloseConnectOfAi2830;
import io.socket.client.Socket;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class Ai2830WebsocketSessionManage {
    /**
     * chatId WebSocketSession
     */
    @Bean
    public ConcurrentHashMap<String, WebSocketSession> Ai2830ChatIdToSession(){
        return new ConcurrentHashMap<>();
    }

    /**
     *  WebSocketSession.getId() chatId
     * */
    @Bean
    public ConcurrentHashMap<String, String> SessionIdToAi2830ChatId(){
        return new ConcurrentHashMap<>();
    }


    /**
     * chatId Socket
     */
    @Bean
    public ConcurrentHashMap<String, Socket> Ai2830ChatIdToSocket(){
        return new ConcurrentHashMap<>();
    }

    /**
     * socket.id() chatId
     */
    @Bean
    public ConcurrentHashMap<String, String> SocketIdToChatId(){
        return new ConcurrentHashMap<>();
    }


    /**
     * 前端建立了websocket连接， 记录当前的chatId，放到队列中，由另外线程连接到AI2830 服务
     * */
    @Bean
    public BlockingQueue<String> FrontNewAi2830ConnectChatId(){
        return new LinkedBlockingQueue<>();
    }

    /**
     *  要断开的socketIo
     */
    @Bean
    public BlockingQueue<Socket> FrontClosedAi2830ConnectSession(){
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public CommandLineRunner ConnectToAi2830Consumer(ConnectToAi2830 connectToAi2830) {
        return args -> {
            Thread thread =  new Thread(connectToAi2830);
            thread.setName("AI2830-connect");
            thread.start();
        };
    }

    @Bean
    public CommandLineRunner CloseConnectOfAi2830Consumer(CloseConnectOfAi2830 closeConnectOfAi2830) {
        return args -> {
            Thread thread =  new Thread(closeConnectOfAi2830);
            thread.setName("XX-AI2830-connect");
            thread.start();
        };
    }
}
