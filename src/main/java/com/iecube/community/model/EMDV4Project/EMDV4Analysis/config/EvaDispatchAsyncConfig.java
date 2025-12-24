package com.iecube.community.model.EMDV4Project.EMDV4Analysis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
public class EvaDispatchAsyncConfig{
    /**
     * 自定义异步线程池
     */
    @Bean(name = "EvaDispatch")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2); // 核心线程数
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 6); // 最大线程数= 核心线程数 × 3  可选区间 核心线程数的2-4倍， 根据单个任务的时长增加倍数
        executor.setQueueCapacity(100); // 队列容量  = 队列容量 = 核心线程数 × 单次任务平均耗时（秒） × 峰值QPS × 缓冲系数（0.5~1） 结果可向上取整
        executor.setKeepAliveSeconds(60); // 空闲线程存活时间
        executor.setThreadNamePrefix("dispatch-eva-"); // 线程名前缀
        // 拒绝策略：当线程池满时，由调用线程处理（如主线程）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
