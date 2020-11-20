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

    public static final String order_dead_release_routing_key = "order.release";

    public static final String order_pay_success_routing_key = "order.payed";

    // 订单支付成功和支付成功扣库存 可以用一个路由键，消息通过一个路由键发送到两个队列中
    // public static final String stock_sub_routing_key = "stock.sub";

    /**
     * 订单的过期时间，30分钟
     */
    public static final Long order_timeout = 1000 * 60 * 30L;


    /**
     * 死单队列
     */
    public static final String order_queue_dead = "order-dead-queue";


    /**
     * 订单释放队列
     */
    public static final String order_queue_need_release = "order-need-release-queue";

    /**
     * 订单支付成功队列
     */
    public static final String order_queue_pay_success = "order-pay-success-queue";

    /**
     * 支付成功，库存扣减队列
     */
    public static final String stock_queue_sub = "stock-sub-queue";
}
