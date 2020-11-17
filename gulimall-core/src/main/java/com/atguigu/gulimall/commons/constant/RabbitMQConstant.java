package com.atguigu.gulimall.commons.constant;

/**
 * @author 10017
 * <p>
 * RabbitMQ组件命名前缀常量类
 */
public class RabbitMQConstant {

    /**
     * 只用一个交换机，根据不同的路由键路由到不同的队列
     */
    public static final String order_exchange = "order_exchange";


    public static final String order_create_event_routing_key = "order.create";

    public static final String order_dead_event_routing_key = "order.dead";

    /**
     * 订单的过期时间，30分钟
     */
    public static final Long order_timeout = 1000 * 60 * 30L;


    /**
     * 死单队列
     */
    public static final String order_queuq_dead = "order-dead-queue";
}
