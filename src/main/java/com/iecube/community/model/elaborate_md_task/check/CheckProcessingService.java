package com.iecube.community.model.elaborate_md_task.check;

import com.iecube.community.model.elaborate_md_task.check.config.CheckConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class CheckProcessingService {

    private final BlockingQueue<Check> taskQueue;
    private final ConcurrentHashMap<String, Check> waitingCheck;

//    @Autowired
//    private CheckConfig checkConfig;

//    @Autowired
//    private CheckConsumer checkConsumer;

//    @Autowired
//    private WaitingCheckWebSocketConsumer waitingCheckWebSocketConsumer;


//    @Autowired
    public CheckProcessingService(BlockingQueue<Check> taskQueue, ConcurrentHashMap<String, Check> waitingCheck) {
        log.info("CheckProcessingService taskQueue ==> {}", System.identityHashCode(taskQueue));
        this.taskQueue = taskQueue;
        this.waitingCheck = waitingCheck;
        log.info("CheckProcessingService this.taskQueue ==> {}", System.identityHashCode(this.taskQueue));
        // 启动消费者线程
//        try{
//            log.info("CheckProcessingService started");
////            new Thread( new CheckConsumer(this.taskQueue, this.waitingCheck)).start();
////            new Thread(waitingCheckWebSocketConsumer).start();
//        }catch (Exception e){
//            log.error("Failed to start consumer thread: {}",e.getMessage());
//        }

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
