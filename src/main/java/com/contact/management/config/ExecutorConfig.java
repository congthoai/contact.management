package com.contact.management.config;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {

    @Value("${execution.pool.size:10}")
    private int poolSize;

    @Bean
    public ScheduledThreadPoolExecutor taskExecutor() {
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
                poolSize, new CallerRunsPolicy());
        executor.allowCoreThreadTimeOut(true);
        executor.setRemoveOnCancelPolicy(true);
        executor.setKeepAliveTime(60L, TimeUnit.SECONDS);

        return executor;
    }
}
