package com.iecube.community.model.elaborate_md_task.check;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class CheckProcessingService {

    private final BlockingQueue<Check> taskQueue;
    private final ConcurrentHashMap<String, Check> waitingCheck;


    public CheckProcessingService(BlockingQueue<Check> taskQueue, ConcurrentHashMap<String, Check> waitingCheck) {
        this.taskQueue = taskQueue;
        this.waitingCheck = waitingCheck;
    }

    /**
     * needCheck 生产者 接收前端的needCheck
     * */
    public void addTask(Check needCheck) {
        log.info(" 生产者 addTask in");
        try {
            log.info("生产者 addTask add {}", needCheck);
            taskQueue.put(needCheck);
        } catch (InterruptedException e) {
            log.info("addTask InterruptedException");
            Thread.currentThread().interrupt();
        }
    }
}
