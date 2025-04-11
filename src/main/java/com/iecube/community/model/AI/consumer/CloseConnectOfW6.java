package com.iecube.community.model.AI.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CloseConnectOfW6 implements Runnable{

    private final ConcurrentHashMap<String, WebSocketSession> W6ChatIdToSession;
    private final ConcurrentHashMap<String, String> W6SessionIdToChatId;
    private final ConcurrentHashMap<String, WebSocketSession> FrontSessionIdToW6Session;
    private final BlockingQueue<WebSocketSession> FrontClosedSession;

    public CloseConnectOfW6(ConcurrentHashMap<String, WebSocketSession> W6ChatIdToSession,
                            ConcurrentHashMap<String, String> W6SessionIdToChatId,
                            ConcurrentHashMap<String, WebSocketSession> FrontSessionIdToW6Session,
                            BlockingQueue<WebSocketSession>FrontClosedSession){
        log.info("close-w6-connect-->start");
        this.W6ChatIdToSession = W6ChatIdToSession;
        this.W6SessionIdToChatId = W6SessionIdToChatId;
        this.FrontSessionIdToW6Session = FrontSessionIdToW6Session;
        this.FrontClosedSession = FrontClosedSession;
    }
    @Override
    public void run() {
        log.info("close-w6-connect-->running");
        //使用异步IO 处理webSocket 连接
        while(true){
            try{
                WebSocketSession session = FrontClosedSession.take();  // 新的断开的前端连接
                WebSocketSession w6Session = FrontSessionIdToW6Session.get(session.getId());
                if(w6Session != null){
                    if(w6Session.isOpen()){
                        try {
                            w6Session.close(new CloseStatus(CloseStatus.NORMAL.getCode()));
                            log.info("w6 webSocket closed because online websocket closed");
                        } catch (IOException e) {
                            log.error("w6 webSocket closed error for online websocket closed",e);
                        }
                    }
                }
                FrontSessionIdToW6Session.remove(session.getId());
                log.info("there are {} in W6ChatIdToSession, {} in W6SessionIdToChatId, {} in FrontSessionIdToW6Session", W6ChatIdToSession.size(), W6SessionIdToChatId.size(), FrontSessionIdToW6Session.size());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
