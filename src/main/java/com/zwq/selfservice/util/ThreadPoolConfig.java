package com.zwq.selfservice.util;

import com.zwq.selfservice.service.BilliardTableService;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    private BilliardTableService billiardTableService;

    @Autowired
    private  BilliardTableService setBilliardTableService(BilliardTableService billiardTableService) {
        return this.billiardTableService = billiardTableService;
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        long count = billiardTableService.count();
        if (count > 0) {
            return Executors.newScheduledThreadPool(Long.valueOf(count).intValue());
        }
        //默认开启二十个线程
        return Executors.newScheduledThreadPool(20);
    }

    @PreDestroy
    public void destroy() {
        ScheduledExecutorService scheduler = scheduledExecutorService();
        scheduler.shutdown(); // 关闭线程池
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow(); // 强制关闭
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }


}
