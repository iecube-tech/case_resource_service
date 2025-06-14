package com.iecube.community.model.Ai2830.consumer;

import io.socket.client.Socket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CloseConnectOfAi2830 implements Runnable{

    private final  BlockingQueue<Socket> FrontClosedAi2830ConnectSession;
    private final ConcurrentHashMap<String, String> SocketIdToChatId;
    private final ConcurrentHashMap<String, Socket> Ai2830ChatIdToSocket;
    public CloseConnectOfAi2830(
            ConcurrentHashMap<String, WebSocketSession> Ai2830ChatIdToSession,
            ConcurrentHashMap<String, String> SessionIdToAi2830ChatId,
            ConcurrentHashMap<String, Socket> Ai2830ChatIdToSocket,
            ConcurrentHashMap<String, String> SocketIdToChatId,
            BlockingQueue<Socket> FrontClosedAi2830ConnectSession
    ){
        log.info("close-Ai2830-connect-->start");
        this.Ai2830ChatIdToSocket = Ai2830ChatIdToSocket;
        this.SocketIdToChatId = SocketIdToChatId;
        this.FrontClosedAi2830ConnectSession = FrontClosedAi2830ConnectSession;
    }

    @Override
    public void run() {
        log.info("close-2830-connect-->running");
        //使用异步IO 处理webSocket 连接
        while(true){
            try{
                Socket socket = FrontClosedAi2830ConnectSession.take();  // 新的需要断开的socketIo连接
                String chatId = this.SocketIdToChatId.get(socket.id());
                log.info("关闭2830AI对话-connect: closing:{},{}",chatId,socket.id());
                this.Ai2830ChatIdToSocket.remove(chatId);
                this.SocketIdToChatId.remove(socket.id());
//                WebSocketSession w6Session = FrontSessionIdToW6Session.get(session.getId());
//                SocketIdToChatId.remove(socket.id());
                if(socket.connected()){
                    socket.disconnect();
                }
                log.info("2830AI连接断开，断开AI2830SocketIo：{}",socket.id());
//                log.info("there are {} in W6ChatIdToSession, {} in W6SessionIdToChatId, {} in FrontSessionIdToW6Session", W6ChatIdToSession.size(), W6SessionIdToChatId.size(), FrontSessionIdToW6Session.size());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
