package com.atguigu.gulimall.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置一组能完成 【死信路由】+【延时队列】功能
 * 进入死信队列的条件：
 *      1.消息被Consumer拒收，且requeue = false，不再重新加入Queue，那么消息将会被加入死信队列
 *      2.消息在Queue中的存活时间到了（给消息设置过期时间 或者 给队列设置过期时间）
 *      3.队列中的长度限制满了，排在前面的消息会被丢弃或者进入死信队列
 */
//@Configuration
@EnableRabbit
public class GulimallDeadExchangeConfig {

    /**
     * 工作于订单创建的交换机
     *
     * @return
     */
    @Bean("orderCreateExchange")
    public Exchange orderCreateExchange() {
        /**
         * String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
         */
        return new DirectExchange("orderCreateExchange", true, false, null);
    }


    /**
     * 用于和死信路由交互的队列,存储还未到过期时间的消息
     * 需要加上一些特殊的参数指定死信路由和路由键
     */
    @Bean("deadOrderStorageQueue")
    public Queue deadOrderStorageQueue() {
        /**
         * String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments
         */
        Map<String, Object> properties = new HashMap<>();
        // 信死了以后发给哪个交换机（死信路由），而不是丢弃
        properties.put("x-dead-letter-exchange", "orderDeadExchange");
        // 信死了以什么路由键发出去，消息发布者需要指定的路由键
        properties.put("x-dead-letter-routing-key", "dead.order");
        properties.put("x-message-ttl", 1000 * 30);
        return new Queue("deadOrderStorageQueue", true, false, false, properties);
    }


    @Bean("deadOrderRoutingBinding")
    public Binding deadOrderRoutingBinding() {
        /**
         * String destination, DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments
         *
         * "create.order"
         */
        return new Binding("deadOrderStorageQueue", Binding.DestinationType.QUEUE, "orderCreateExchange", "create.order", null);
    }

    //-------------------以上的交换机/路由绑定/队列 为订单创建后进入的队列，队列有设置的ttl时间，以及死信生成后被发送到的交换机和使用的路由键-------------------
    //-------------------订单创建的信息能保存到与死信路由交互的队列中-------------------

    /**
     * 订单到达过期时间后需要经过的交换机，死信路由（死信交换机）
     */
    @Bean("orderDeadExchange")
    public Exchange orderDeadExchange() {
        /**
         * String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
         */
        return new DirectExchange("orderDeadExchange", true, false, null);
    }


    /**
     * 最后被监听的队列，最后被消费的队列
     *
     * @return
     */
    @Bean("closeOrderQueue")
    public Queue deadOrderQueue() {
        /**
         * String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments
         */
        return new Queue("closeOrderQueue", true, false, false, null);
    }

    @Bean("deadBinding")
    public Binding deadBinding() {
        /**
         * String destination, DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments
         */
        return new Binding("closeOrderQueue", Binding.DestinationType.QUEUE, "orderDeadExchange", "dead.order", null);
    }
}
