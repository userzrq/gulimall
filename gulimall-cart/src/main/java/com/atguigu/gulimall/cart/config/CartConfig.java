package com.atguigu.gulimall.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class CartConfig {

    /**
     * int corePoolSize,
     * int maximumPoolSize,
     * long keepAliveTime,
     * TimeUnit unit,
     * BlockingQueue<Runnable> workQueue
     *
     * @return
     * @Primary 主线程池 核心业务线程池 Autowired时不Qualifier  默认会选择 @Primary 标注的
     */
    @Bean("mainExecutor")
    @Primary
    public ThreadPoolExecutor mainThreadPoolExecutor() {
        // 不要轻易使用无界队列，会很容易把内存耗尽
        /**
         *  LinkedBlockingQueue 在不设置容量时默认采用 Integer.MAX_VALUE ，是一个无界队列
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                8,
                1000,
                0L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(Integer.MAX_VALUE / 2)
        );
        return executor;
    }


    /**
     * 非核心业务线程池
     */
    @Bean("otherExecutor")
    public ThreadPoolExecutor noMainThreadPoolExecutor() {
        // 不要轻易使用无界队列，会很容易把内存耗尽
        /**
         *  LinkedBlockingQueue 在不设置容量时默认采用 Integer.MAX_VALUE ，是一个无界队列
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                8,
                1000,
                0L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(Integer.MAX_VALUE / 2)
        );
        return executor;
    }
}
