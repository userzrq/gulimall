package com.atguigu.gulimall.oms.service.impl;

import com.atguigu.gulimall.commons.constant.RabbitMQConstant;
import com.atguigu.gulimall.commons.constant.RedisPrefixConstant;
import com.atguigu.gulimall.commons.to.mq.OrderItemMqTo;
import com.atguigu.gulimall.commons.to.mq.OrderMqTo;
import com.atguigu.gulimall.oms.dao.OrderDao;
import com.atguigu.gulimall.oms.dao.OrderItemDao;
import com.atguigu.gulimall.oms.entity.OrderEntity;
import com.atguigu.gulimall.oms.enume.OrderStatusEnume;
import com.atguigu.gulimall.oms.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * 监听秒杀成功的队列，并快速创建订单,创建完订单后将CountDownLatch中订单的闭锁-1
 *
 * @author 10017
 */
@Service
@Slf4j
public class OrderRabbitMQListenerService {

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;


    @RabbitListener(queues = {RabbitMQConstant.order_queue_quick_create})
    public void killedOrder(Message message, Channel channel, OrderMqTo orderMqTo) throws IOException {

        try {

            String orderSn = orderMqTo.getOrderSn();
            List<OrderItemMqTo> orderItems = orderMqTo.getOrderItems();
            Long memberId = orderMqTo.getMemberId();

            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setMemberId(memberId);
            orderEntity.setOrderSn(orderSn);
            orderEntity.setStatus(OrderStatusEnume.UNPAY.getCode());

            BigDecimal totalAmount = new BigDecimal(0.00);
            for (OrderItemMqTo orderItem : orderItems) {
                Long skuId = orderItem.getSkuId();

                // TODO 拿skuId去数据库中查,添加到订单总额中
                // totalAmount.add();
            }

            orderEntity.setTotalAmount(totalAmount);
            // 数据库保存订单
            int insert = orderDao.insert(orderEntity);

            // 用户点击支付时，可以根据订单号找到数据库中未支付的订单，生成支付页进行支付

            // 保存订单项
            // orderItemDao.insert(orderItems);

            if(insert == 1){
                RCountDownLatch latch = redisson.getCountDownLatch(RedisPrefixConstant.MIAO_SHA_ORDERSN_COUNTDOWN + orderSn);
                latch.countDown();
            }


            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
