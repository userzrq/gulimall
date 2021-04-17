package com.atguigu.gulimall.order.service.impl;

import com.atguigu.gulimall.commons.constant.RabbitMQConstant;
import com.atguigu.gulimall.commons.to.mq.OrderMqTo;
import com.atguigu.gulimall.order.enume.OrderStatusEnume;
import com.atguigu.gulimall.order.feign.OrderCreateFeignService;
import com.atguigu.gulimall.order.vo.order.OrderCloseVo;
import com.atguigu.gulimall.order.vo.order.OrderEntityVo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

/**
 * @author 10017
 */

@Slf4j
@Service
public class OrderRabbitListenerService {

    @Autowired
    private OrderCreateFeignService orderCreateFeignService;

    @Autowired
    private RabbitTemplate rabbitTemplate;


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
    @RabbitListener(queues = {RabbitMQConstant.order_queue_dead})
    public void closeOrder(Message message, Channel channel, OrderMqTo orderMqTo) throws IOException {

        // 最终一致性
        try {
            Long id = orderMqTo.getId();
            String orderSn = orderMqTo.getOrderSn();

            // 订单创建成功，判断订单的状态，修改订单状态，并发送到解锁库存的队列，这在死信队列中的保留时间应该为30min
            if (id != null) {
                log.info("订单【" + id + "】在队列中超时，正在准备关闭...");
                OrderEntityVo data = orderCreateFeignService.info(id).getData();

                // 在这里肯定要判断订单的支付情况，如果已经支付就不走下面的逻辑
                if (Objects.nonNull(data) && OrderStatusEnume.UNPAY.getCode().equals(data.getStatus())) {
                    log.info("订单【" + id + "】由于未支付原因，正在准备关闭...");
                    // 订单的状态为未支付时，关闭订单
                    OrderCloseVo orderCloseVo = new OrderCloseVo();
                    orderCloseVo.setId(data.getId());
                    orderCloseVo.setStatus(OrderStatusEnume.CLOSED.getCode());

                    orderCreateFeignService.closeOrder(orderCloseVo);

                    log.info("订单【" + id + "】已关闭，即将为其解锁库存...");
                    rabbitTemplate.convertAndSend(RabbitMQConstant.order_exchange, RabbitMQConstant.order_dead_release_routing_key, orderMqTo);
                }
                // 订单创建异常，但是库存已经锁了
                // 但是应该要去看看在锁完库存之后，订单支付成功了没有
            } else {
                // 没有订单id的情况，这种情况就是订单创建失败了，在死信队列中的保留时间应该为40min
                log.info("订单Token【" + orderSn + "】加锁了库存，但订单创建失败了...开始解锁库存");
                rabbitTemplate.convertAndSend(RabbitMQConstant.order_exchange, RabbitMQConstant.order_dead_release_routing_key, orderMqTo);
            }

            // ack消息，只ack单条
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("关闭订单操作失败..." + e.getMessage());
            // 如果操作失败，重新将消息发回队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
