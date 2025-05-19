package com.iecube.community.model.Ai2830.WebSocketConfig.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.Ai2830.api.service.Ai2830ApiService;
import com.iecube.community.model.Ai2830.dto.AI2830Msg;
import com.iecube.community.model.Ai2830.dto.StreamAi2830Msg;
import io.socket.client.Socket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.DataInput;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * ai2830 websocket 处理
 */
@Slf4j
@Component
public class Ai2830WebsocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, WebSocketSession> Ai2830ChatIdToSession;
    private final ConcurrentHashMap<String, String> SessionIdToAi2830ChatId;
    private final BlockingQueue<String> FrontNewAi2830ConnectChatId; // 新要建立的socketIo连接
    private final ConcurrentHashMap<String, Socket> Ai2830ChatIdToSocket;
    private final BlockingQueue<Socket> FrontClosedAi2830ConnectSession;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    @Autowired
    public Ai2830WebsocketHandler(ConcurrentHashMap<String, WebSocketSession> Ai2830ChatIdToSession,
                                  ConcurrentHashMap<String, String> SessionIdToAi2830ChatId,
                                  BlockingQueue<String> FrontNewAi2830ConnectChatId,
                                  ConcurrentHashMap<String, Socket> Ai2830ChatIdToSocket,
                                  BlockingQueue<Socket> FrontClosedAi2830ConnectSession){
        this.Ai2830ChatIdToSession = Ai2830ChatIdToSession;
        this.SessionIdToAi2830ChatId = SessionIdToAi2830ChatId;
        this.FrontNewAi2830ConnectChatId = FrontNewAi2830ConnectChatId;
        this.Ai2830ChatIdToSocket = Ai2830ChatIdToSocket;
        this.FrontClosedAi2830ConnectSession = FrontClosedAi2830ConnectSession;
    }

    @Autowired
    private Ai2830ApiService ai2830ApiService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        URI uri = session.getUri();
        if (uri != null) {
            String path = uri.toString();
            String chatId = path.substring(path.lastIndexOf('/') + 1);
            session.setTextMessageSizeLimit(10485760);
            // 判断之前有没有这个chatId的连接，有则关闭旧连接 并不许重连
            WebSocketSession oldSession = Ai2830ChatIdToSession.get(chatId);
            if (oldSession != null) {
                oldSession.close(CloseStatus.GOING_AWAY);
                TimeUnit.MILLISECONDS.sleep(100);
            }
            Ai2830ChatIdToSession.put(chatId, session);
            SessionIdToAi2830ChatId.put(session.getId(), chatId);
            FrontNewAi2830ConnectChatId.put(chatId); //队列
            log.info("online websocket: {}, webS-->{}, ",  chatId, session.getId());
            // todo current
            int maxPage = 100;
            for(int page=1; page<maxPage; page++){
                try{
                    JsonNode res = ai2830ApiService.getUserIdMessages(chatId,"2830", page);
                    if(res.get("messages")!=null){
                        StreamAi2830Msg currentMsg = new StreamAi2830Msg();
                        currentMsg.setType("current");
                        currentMsg.setMsgList(new ObjectMapper().treeToValue(res.get("messages"), List.class));
                        session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(currentMsg)));
                    }
                    if(!res.get("has_next").asBoolean()){
                        break;
                    }
                }catch (IOException e){
                    log.error("获取历史数据IO异常，{}",e.getMessage());
                }
            }
        }else {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String chatId = SessionIdToAi2830ChatId.get(session.getId());

        if(Ai2830ChatIdToSession.get(chatId) == session){
            Ai2830ChatIdToSession.remove(chatId); // web chtId session
        }
        Socket socketIo2830 = Ai2830ChatIdToSocket.get(chatId);
        System.out.println(socketIo2830);
        if(socketIo2830!=null){
            FrontClosedAi2830ConnectSession.put(socketIo2830);
            Ai2830ChatIdToSocket.remove(chatId);// chatId ai2830Socket
        }
        SessionIdToAi2830ChatId.remove(session.getId());
        log.info("online websocket: {} closed，{}, {}, {}", chatId, session.getId(), status.getCode(), status.getReason());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
//        System.out.println(message.getPayload());
        AI2830Msg msg = parseMessage(session, message.getPayload());
        // type 的消息用于ping
        if(msg.getType()==null){
            //message-ack
            String chatId = SessionIdToAi2830ChatId.get(session.getId());
            Socket socket = Ai2830ChatIdToSocket.get(chatId);
            msg.setDirection("user");
            msg.setTimestamp(LocalDateTime.now().format(formatter));
            StreamAi2830Msg ackMsg = new StreamAi2830Msg();
            ackMsg.setType("message-ack");
            ackMsg.setMessage(msg);
            try {
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(ackMsg)));
            }catch (IOException e){
                log.error("转发AI2830消息json格式化异常: message-ack : {}", e.getMessage());
            }
//        log.info("需要转发到{}",socket);
            // 获取socketIo 转发到 socketIo
            Map<String, Object> requestBodyMap = new HashMap<>();
            requestBodyMap.put("user_id", chatId);
            requestBodyMap.put("content", msg.getMessage());
            requestBodyMap.put("course_id", msg.getCourse_id());
            socket.emit("send_message",requestBodyMap);
//        connectToAi2830.sendMessage(msg.getChatId(), msg.getContent(), msg.getCourseId(),msg.getTeacherType());
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
    }


    private AI2830Msg parseMessage(WebSocketSession session, String payload){
        AI2830Msg msg = new AI2830Msg();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode json = objectMapper.readTree(payload);
            msg.setUser_id(SessionIdToAi2830ChatId.get(session.getId()));
            msg.setMessage(json.get("message")==null?null:json.get("message").asText());
            msg.setCourse_id(json.get("course_id")==null?null:json.get("course_id").asText());
            msg.setTeacher_type(json.get("teacher_type")==null?null:json.get("teacher_type").asText());
            msg.setType(json.get("type")==null?null:json.get("type").asText());
        } catch (JsonProcessingException e) {
            log.warn("解析数据异常");
        }
        return msg;
    }
}
