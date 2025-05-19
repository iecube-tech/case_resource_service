package com.iecube.community.model.Ai2830.cilent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
public class Ai2830EventHandler {
    private final ConnectToAi2830T connectToAi2830T;

    @Autowired
    public Ai2830EventHandler(ConnectToAi2830T connectToAi2830T) {
        this.connectToAi2830T = connectToAi2830T;
    }

    @PostConstruct
    public void init() {
        // 注册自定义事件处理器
        registerEventHandlers();
    }


    private void registerEventHandlers() {
        // 监听服务器消息
        log.info("注册Ai2830消息监听....");
        connectToAi2830T.getSocket().on("message", args -> {
           System.out.println(args);
           if (args != null && args.length > 0) {
               String message = (String) args[0];
               log.info("收到2830AI服务消息: {}", message);
           }
        });
    }
}
