package com.iecube.community.model.elaborate_md_task.check.config;

import com.iecube.community.model.elaborate_md_task.check.Check;
import com.iecube.community.model.elaborate_md_task.check.CheckConsumer;
import com.iecube.community.model.elaborate_md_task.check.WaitingCheckWebSocketConsumer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class CheckConfig {

    @Bean
    public BlockingQueue<Check> taskQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public BlockingQueue<String> chatIdQueue(){
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public ConcurrentHashMap<String, Check> waitingCheck() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public CommandLineRunner startCheckConsumer(CheckConsumer checkConsumer) {
        return args -> {
            Thread thread =  new Thread(checkConsumer);
            thread.setName("check-C");
            thread.start();
        };
    }

    @Bean
    public CommandLineRunner startWaitingCheckWebSocketConsumer(WaitingCheckWebSocketConsumer waitingCheckWebSocketConsumer) {
        return args -> {
            Thread thread =  new Thread(waitingCheckWebSocketConsumer);
            thread.setName("check-W-C");
            thread.start();
        };
    }
}
