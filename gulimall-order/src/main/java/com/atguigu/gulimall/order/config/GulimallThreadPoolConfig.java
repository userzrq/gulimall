package com.atguigu.gulimall.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Gulimall-订单模块线程池配置：解决多远程调用时单线程阻塞
 *
 * @author userzrq
 */
@Configuration
public class GulimallThreadPoolConfig {

    /**
     * spring.redis.host=dingyue.redis.cache.windows.net
     * spring.redis.port=6380
     *
     * @return
     */
    @Bean
    public JedisPool jedisPool() {
        JedisPool jedisPool = new JedisPool("dingyue.redis.cache.windows.net", 6380);
        return jedisPool;
    }


    @Bean
    @Primary
    public ThreadPoolExecutor executor() {
        /**
         * int corePoolSize,
         * int maximumPoolSize,
         * long keepAliveTime,
         * TimeUnit unit,
         * BlockingQueue<Runnable> workQueue,
         *
         * ThreadFactory threadFactory,
         * RejectedExecutionHandler handler
         */
        return new ThreadPoolExecutor(10, 1000, 0L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000000));

        // 最大容量为 1000 + 1000000
    }


}
