package com.atguigu.gulimall.order.service.impl;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.constant.RabbitMQConstant;
import com.atguigu.gulimall.commons.to.mq.OrderMqTo;
import com.atguigu.gulimall.order.enume.OrderStatusEnume;
import com.atguigu.gulimall.order.feign.OrderCreateFeignService;
import com.atguigu.gulimall.order.vo.order.OrderCloseVo;
import com.atguigu.gulimall.order.vo.order.OrderEntityVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

/**
 * @author 10017
 */

@Service
public class OrderRabbitListenerService {

    @Autowired
    private OrderCreateFeignService orderCreateFeignService;


    /**
     * RabbitMQ:
     * unack状态的消息,无论消费掉线还是RabbitMQ停机，消息都会进行持久化
     * 在下次启动的时候重新变成ready状态发给消费者
     *
     * @param message
     * @param channel
     * @param orderMqTo
     * @throws IOException
     */
    @RabbitListener(queues = {RabbitMQConstant.order_queuq_dead})
    public void closeOrder(Message message, Channel channel, OrderMqTo orderMqTo) throws IOException {
        try {
            Long id = orderMqTo.getId();
            OrderEntityVo data = orderCreateFeignService.info(id).getData();

            if (Objects.isNull(data) && OrderStatusEnume.UNPAY.getCode().equals(data.getStatus())) {
                // 订单的状态为未支付时，关闭订单
                OrderCloseVo orderCloseVo = new OrderCloseVo();
                orderCloseVo.setId(data.getId());
                orderCloseVo.setStatus(OrderStatusEnume.CLOSED.getCode());

                orderCreateFeignService.closeOrder(orderCloseVo);
            }

            // ack消息，只ack单条
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // 如果操作失败，重新将消息发回队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
