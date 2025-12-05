package com.iecube.community.model.exportProgress.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "exportTaskExecutor")
    public ThreadPoolTaskExecutor exportTaskExecutor() {
        int cpuCore = Runtime.getRuntime().availableProcessors();
        int corePoolSize = 2 * cpuCore + 1; // 核心线程数
        int maxPoolSize = 3* cpuCore; // 固定线程池（无扩容，减少线程切换开销）
        int queueCapacity = 200; // 任务队列容量（根据业务调整，如100-1000，避免无界队列OOM）
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(16);
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("ExportTask-");
        executor.setKeepAliveSeconds(60);
        executor.initialize();
        return executor;
    }
}