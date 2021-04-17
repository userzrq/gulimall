package com.atguigu.gulimall.wms.service;


import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.constant.RabbitMQConstant;
import com.atguigu.gulimall.commons.to.mq.OrderMqTo;
import com.atguigu.gulimall.commons.to.order.OrderItemVo;
import com.atguigu.gulimall.commons.to.order.OrderVo;
import com.atguigu.gulimall.wms.dao.WareSkuDao;
import com.atguigu.gulimall.wms.feign.OrderFeignService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 支付成功后真正扣库存的队列
 *
 * @author 10017
 */
@Slf4j
@Service
public class WareRabbitListener {

    @Autowired
    private OrderFeignService orderFeignService;

    @Autowired
    private WareSkuDao wareSkuDao;

    @RabbitListener(queues = {RabbitMQConstant.stock_queue_sub})
    public void orderPayed(Message message, Channel channel, OrderMqTo orderMqTo) throws IOException {

        try {
            String orderSn = orderMqTo.getOrderSn();
            Resp<OrderVo> orderInfo = orderFeignService.getOrderInfo(orderSn);

            // 同时redis中的锁库存的那个信息也要删除

            // 获取到需要执行减库存的商品信息
            List<OrderItemVo> orderItems = orderInfo.getData().getOrderItems();

            for (OrderItemVo orderItem : orderItems) {
                Long skuId = orderItem.getSkuId();
                Integer skuQuantity = orderItem.getSkuQuantity();

                // 少了一个仓库Id
                log.info("商品【" + skuId + "】锁定了【" + skuQuantity + "】件...正在减库存");
            }


            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
