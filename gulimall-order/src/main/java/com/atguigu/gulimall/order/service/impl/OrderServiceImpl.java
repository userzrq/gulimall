package com.atguigu.gulimall.order.service.impl;

import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.Order;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
public class OrderServiceImpl implements OrderService {

    /**
     * RabbitTemplate：用于收发消息
     * AmqpAdmin：管理RabbitMQ中exchange, queue, binding的创建、删除
     */
    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // ScheduledThreadPool 可调度的线程池
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    @Override
    public Order createOrder() {

        Order order = new Order();
        order.setOrderId(IdWorker.getId());
        order.setDesc("商品xxxxxx-xxxxx");
        order.setStatus(0);

        // 订单创建完，将订单信息发送给MQ
        // String exchange, String routingKey, final Object object
         rabbitTemplate.convertAndSend("orderCreateExchange","create.order",order);

        /**
         * 定时线程池执行，但是没有持久化，MQ的优势在于不依赖于系统的持久化
         */
        executorService.schedule(()->{
            System.out.println(order+"已经过期,正准备查询数据库，决定是否关单");
        },30, TimeUnit.SECONDS);


        return order;
    }

    /**
     * 监听最后的订单队列closeOrderQueue
     */
    @RabbitListener(queues = "closeOrderQueue")
    public void closeOrder(Order order, Channel channel, Message message) throws IOException {

        System.out.println("收到的订单" + order.toString());

        /**
         * 拿到订单的id 去数据库里中查询支付状态，支付状态为未支付的，就关单并解库存
         */
        Long orderId = order.getOrderId();
        System.out.println("正在数据库中查询订单号【" + orderId + "】的支付状态:" + order.getStatus());

        if (order.getStatus() != 1) {
            System.out.println("该订单没有被支付，准备关单，数据库状态改为 -1");
        }

        // 给MQ回复，我们已经处理完此消息了
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }
}
