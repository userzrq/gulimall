package com.atguigu.gulimall.pms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableDiscoveryClient  // 开启nacos服务注册发现功能
@EnableFeignClients(basePackages = "com.atguigu.gulimall.pms.feign")     // 开启远程调用,并指定feign的包
public class PmsCloudConfig {

    /**
     * 自定义线程池
     * @param corePoolSize
     * @param maxPoolSize
     * @return
     */
    @Bean("mainThreadPool")
    public ThreadPoolExecutor threadPool(
            @Value("${app.main.thread.corepoolsize}") Integer corePoolSize,
            @Value("${app.main.thread.maxpoolsize}") Integer maxPoolSize) {

        /**
         * 7 大参数
         * int corePoolSize
         * int maximumPoolSize
         * long keepAliveTime
         * TimeUnit unit
         * BlockingQueue<Runnable> workQueue
         * ThreadFactory threadFactory
         * RejectedExecutionHandler handler
         */
        return new ThreadPoolExecutor(corePoolSize,
                maxPoolSize,
                0L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(Integer.MAX_VALUE / 2));
    }
}
