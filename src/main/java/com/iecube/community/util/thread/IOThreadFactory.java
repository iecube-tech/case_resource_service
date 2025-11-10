package com.iecube.community.util.thread;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IOThreadFactory {
    public static ExecutorService getExecutor(){
        // 1. 自定义线程工厂（给线程命名，方便日志排查）
        ThreadFactory fileProcessThreadFactory = new ThreadFactory() {
            private final AtomicInteger threadNum = new AtomicInteger(1); // 原子计数器，避免线程名重复
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("pdf-gen-" + threadNum.getAndIncrement()); // 线程名：业务标识+序号
                thread.setDaemon(false); // 非守护线程（避免主线程退出时强制中断文件处理）
                return thread;
            }
        };
        // 2. 计算核心参数（IO密集型）
        int cpuCore = Runtime.getRuntime().availableProcessors();
        int corePoolSize = 2 * cpuCore + 1; // 核心线程数
        int maxPoolSize = 3* cpuCore; // 固定线程池（无扩容，减少线程切换开销）
        long keepAliveTime = 60; // 空闲线程存活60秒
        TimeUnit unit = TimeUnit.SECONDS;
        int queueCapacity = 500; // 任务队列容量（根据业务调整，如100-1000，避免无界队列OOM）
        // 3. 手动创建线程池（核心优化）
        // 有界队列，控制任务堆积
        // 自定义线程工厂
        // 拒绝策略：提交线程自己执行
        ExecutorService e = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                unit,
                new ArrayBlockingQueue<>(queueCapacity), // 有界队列，控制任务堆积
                fileProcessThreadFactory, // 自定义线程工厂
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：提交线程自己执行
        );
//        ((ThreadPoolExecutor) e).allowCoreThreadTimeOut(true);
        return e;
    }
}
