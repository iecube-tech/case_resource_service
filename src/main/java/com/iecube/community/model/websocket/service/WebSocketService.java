package com.iecube.community.model.websocket.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iecube.community.model.task.service.TaskService;
import com.iecube.community.model.websocket.message.Message3835;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static javax.websocket.CloseReason.CloseCodes.*;

@Slf4j
@ServerEndpoint(value = "/{type}/{id}")
@Component
public class WebSocketService {

    private static final String USER="online";
    private static final String DEVICE="3835";

    private static final String PING="ping";

    private static final Map<Integer,Session> onlineUser = new ConcurrentHashMap<>(); // 存放iecube.online的建立连接的用户
    private static final Map<Integer,Message3835> onlineTask = new ConcurrentHashMap<>(); // 存放浏览器发来的消息，确定是哪一个实验
    private static final Map<Integer,Session> onlineDevice = new ConcurrentHashMap<>(); // 存放登录在3835软件且连接的用户，即3835软件在线
    private static final Map<Integer,Message3835> onlineDeviceSn = new ConcurrentHashMap<>(); // 存放在线3835的sn号

    private String type;
    private Integer userId;


    @OnOpen
    public void onOpen(Session session, @PathParam("type")String type, @PathParam("id") Integer userId){
        //连接之后保存连接数据
        log.info("onlineUser:"+onlineUser);
        log.info("onlineTask:"+onlineTask);
        log.info("onlineDevice:"+onlineDevice);
        log.info("onlineDevice:"+onlineDeviceSn);
        log.info(type+"-"+userId);
        if(type.equals(USER)){
            onlineUser.remove(userId);
            onlineUser.put(userId, session); // 把用户和建立socket连接的session关联起来
            log.info("学生"+userId+"：使用浏览器建立了socket连接");
            this.type=type;
            this.userId=userId;
            log.info("onlineUser:"+onlineUser);
            log.info("onlineTask:"+onlineTask);
        }
        else if(type.equals(DEVICE)){
            onlineDevice.remove(userId);
            onlineDevice.put(userId,session);
            log.info("学生"+userId+"：使用3835建立了socket连接");
            this.type=type;
            this.userId=userId;
            log.info("onlineDevice:"+onlineDevice);
            log.info("onlineDevice:"+onlineDeviceSn);
        }else {
            log.error("错误的连接请求！");
            try {
                session.close(new CloseReason(GOING_AWAY, "错误的连接请求！"));
            } catch (Exception e) {
                log.error("关闭错误的socket连接发生异常：" + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    @OnMessage
    public void onMessage(String message){
        Message3835 msg = JSON.parseObject(message, Message3835.class);
//        log.info(msg.toString());
        if (msg.getFrom().equals(PING)){
            try{
                Session webSession = onlineUser.get(this.userId);
                Message3835 message3835 = new Message3835();
                message3835.setFrom("pong");
                webSession.getBasicRemote().sendText(sendToOnlineMessage(message3835));
            }catch (Exception e){
                log.warn("pong 失败");
                e.printStackTrace();
            }
            return;
        }
        //1. 收到浏览器的信息
        if(msg.getFrom().equals(USER)){
            // 第一步 浏览器上线 同步信息
            log.info("this.userId:"+this.userId+" msg.getUserId():"+msg.getUserId());
            if(this.userId.toString().equals(msg.getUserId().toString())){
                onlineTask.remove(this.userId);
                onlineTask.put(this.userId,msg);
                log.info("onlineTask:"+onlineTask);
            }
            if(msg.getLogup()!=null){
                if(msg.getLogup()){
                    // 触发提交事件消息  仅做转发
                    if(onlineDeviceSn.get(msg.getUserId())!=null){
                        try{
                            Session deviceSession = onlineDevice.get(this.userId);
                            deviceSession.getBasicRemote().sendText(sentTo3835Message(msg));
                        }catch (Exception e){
                            log.error("转发提交消息失败"+e.getMessage());
                        }

                    }
                    return;
                }
            }
            if(msg.getLock()!=null){
                if(!msg.getLock()){
                    if(onlineDeviceSn.get(msg.getUserId())!=null){
                        // 鉴定为浏览器重连情况
                        Session webSession = onlineUser.get(this.userId);
                        Session deviceSession = onlineDevice.get(this.userId);
                        try{
                            Message3835 reConnectMessage = onlineDeviceSn.get(this.userId);
                            reConnectMessage.setFrom("server");
                            reConnectMessage.setLock(false);
                            webSession.getBasicRemote().sendText(sendToOnlineMessage(reConnectMessage));
                            deviceSession.getBasicRemote().sendText(sentTo3835Message(reConnectMessage));
                            log.info("浏览器重连，已发送重连信息"+reConnectMessage);
                        }catch (Exception e){
                            log.error("浏览器重新上线，发送重连信息失败，重连失败"+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }else{
                    // 第三步 向设备推送已锁定的消息
                    Session deviceSession = onlineDevice.get(this.userId);
                    try{
                        deviceSession.getBasicRemote().sendText(sentTo3835Message(msg));
                        onlineDeviceSn.get(this.userId).setProjectId(msg.getProjectId());
                        onlineDeviceSn.get(this.userId).setPstId(msg.getPstId());
                        onlineDeviceSn.get(this.userId).setTaskNum(msg.getTaskNum());
                        onlineDeviceSn.get(this.userId).setLock(msg.getLock());
                        log.info("学生"+this.userId+"的浏览器接受连接，实验界面已锁定，已发送消息到3835设备");
                    }catch (Exception e){
                        log.info("学生"+this.userId+"的浏览器实验界面已锁定，但是向3835设备同步消息时发生异常"+e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }

        //2. 收到3835的消息
        if(msg.getFrom().equals(DEVICE)){
            if(msg.getLogup()!=null && !msg.getLogup() && msg.getUserId()!=null){
                if(onlineTask.get(msg.getUserId())!=null){
                    try{
                        Session webSession = onlineUser.get(this.userId);
                        webSession.getBasicRemote().sendText(sendToOnlineMessage(msg));
                        log.info("3835已处理提交事件，同步浏览器");
                    }catch (Exception e){
                        log.error("转发提交消息失败"+e.getMessage());
                    }
                    return;
                }
            }
            // 确定3835的消息是请求连接还是完成实验断开连接
            if(msg.getPstId()==null && msg.getLock()){
                // 请求连接消息
                //这里要首先确保浏览器的在线状态，并确定有实验信息, 给设备返回消息
                // 浏览器不在线
                if(onlineTask.get(msg.getUserId())==null){
                    Session deviceSession = onlineDevice.get(this.userId);
                    log.info("接收到设备请求浏览器数据，但浏览器端未在线，已返回浏览器不在线消息,将断开连接");
                    try{
                        deviceSession.close(new CloseReason(TRY_AGAIN_LATER,"浏览器端不在线，请登录浏览器端再试"));
                    }catch(Exception e){
                        log.warn("断开连接时发生异常"+e.getMessage());
                        e.printStackTrace();
                    }
                    return;
                }
                //浏览器在线，这里应该携带设备的额sn号，且申请lock 并向浏览器发送消息，保存设备信息
                if(msg.getSnId()!=null && msg.getLock()){
                    onlineDeviceSn.put(this.userId, msg);
                    log.info(onlineDeviceSn.toString());
                    log.info("onlineDevice:"+onlineDevice);
                    log.info("onlineDevice:"+onlineDeviceSn);
                    Session webSession = onlineUser.get(this.userId);
                    try {
                        webSession.getBasicRemote().sendText(sendToOnlineMessage(msg));
                        log.info("接收到设备请求浏览器数据，浏览器在线，设备在线，已发送至浏览器。");
                    }catch (Exception e){
                        log.warn("接收到设备请求浏览器数据，向浏览器发送数据异常，关闭设备连接"+e.getMessage());
                        e.printStackTrace();
                        try{
                            Session deviceSession = onlineDevice.get(this.userId);
                            deviceSession.close(new CloseReason(TRY_AGAIN_LATER,"向浏览器端同步数据失败，请稍后重试"));
                        } catch (Exception e1){
                            log.warn("断开连接时发生异常"+e1.getMessage());
                            e1.printStackTrace();
                        }
                    }
                    return;
                }
                // 收到一条没有意义的来自3835的数据，关闭连接
                try{
                    Session deviceSession = onlineDevice.get(this.userId);
                    deviceSession.close(new CloseReason(CANNOT_ACCEPT, "发送的消息参数错误，不能处理"));
                }catch (Exception e){
                    log.warn("关闭无意义3835连接异常"+e.getMessage());
                    e.printStackTrace();
                }
            }

            if(msg.getPstId()!=null && msg.getLock()==false){
                // 实验完成断开连接请求
//                try{
//                    Session webSession = onlineUser.get(this.userId);
//                    webSession.getBasicRemote().sendText(sendToOnlineMessage(msg));
//                log.info("学生"+this.userId+"的3835发送结束实验请求，断开3835连接");
//                }catch (Exception e){
//                    log.error("学生"+this.userId+"的设备向浏览器同步操作完成结束实验失败，转发结束信息失败"+e.getMessage());
//                    e.printStackTrace();
//                }

                try{
                    onlineDeviceSn.put(this.userId,msg);
                    Session deviceSession = onlineDevice.get(this.userId);
                    deviceSession.close(new CloseReason(NORMAL_CLOSURE, "操作结束，关闭连接"));
                }catch (Exception e){
                    log.warn("学生"+this.userId+"的设备断开连接时产生异常"+e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public String sendToOnlineMessage(Message3835 message3835){
        String msg = JSONObject.toJSONString(message3835);
        return msg;
    }

    public String sentTo3835Message(Message3835 message3835){
        String msg = JSONObject.toJSONString(message3835);
        return msg;
    }

    @OnClose
    public void onClose(Session session){
        if(this.type.equals(USER)){
            if(onlineDeviceSn.get(this.userId)!=null){
                // 浏览器在设备在线的时候断连
                try{
                    Message3835 msg = new Message3835();
                    msg.setFrom("server");
                    msg.setIsConnecting(false);
                    msg.setUserId(this.userId);
                    Session deviceSession = onlineDevice.get(this.userId);
                    deviceSession.getBasicRemote().sendText(sentTo3835Message(msg));
                    log.info("学生"+this.userId+"的浏览器在设备在线时断开了socket连接已通告3835");
                }catch (Exception e){
                    log.warn("学生"+userId+"的浏览器在设备在线时断开了socket连接,通告设备时发生异常"+e.getMessage());
                    e.printStackTrace();
                }
            }
            if (session.equals(onlineUser.get(this.userId))){
                onlineUser.remove(this.userId);
                onlineTask.remove(this.userId);
            }
            log.info("学生"+userId+"的浏览器断开了socket连接");
            log.info("onlineUser:"+onlineUser);
            log.info("onlineTask:"+onlineTask);
        }
        if(this.type.equals(DEVICE)){
            //设备断连
            if(onlineTask.get(this.userId)!=null){
                try{
                    Message3835 msg = onlineDeviceSn.get(this.userId);
                    msg.setFrom("server");
                    msg.setIsConnecting(false);
                    Session webSession = onlineUser.get(this.userId);
                    webSession.getBasicRemote().sendText(sendToOnlineMessage(msg));
                    if(msg.getLock()==true){
                        log.info("学生"+this.userId+"的设备掉线,已同步浏览器");
                    }else {
                        log.info("学生"+this.userId+"的3835发送结束实验请求，已同步至浏览器，断开3835连接");
                    }

                }catch (Exception e){
                    log.warn("学生"+this.userId+"的设备掉线，向浏览器同步消息发生异常,");
                    e.printStackTrace();
                }
            }
            log.info("学生"+this.userId+"的3835设备掉线");
            onlineDevice.remove(this.userId);
            onlineDeviceSn.remove(this.userId);
            log.info("onlineDeviceSn:"+onlineDeviceSn);
            log.info("onlineDevice:"+onlineDevice);
        }
    }
}
