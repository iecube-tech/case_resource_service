package com.iecube.community.model.exportProgress.midware;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ProgressManager {

    @Bean
    public ConcurrentHashMap<String, Boolean> cancelFlags(){
        // 用于存储任务取消状态
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, Integer> progressRate(){
        // 存储任务进度
        return new ConcurrentHashMap<>();
    }
}
