package com.atguigu.gulimall.ums.service;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.constant.RabbitMQConstant;
import com.atguigu.gulimall.commons.to.mq.OrderMqTo;
import com.atguigu.gulimall.commons.to.order.OrderItemVo;
import com.atguigu.gulimall.commons.to.order.OrderVo;
import com.atguigu.gulimall.ums.dao.MemberDao;
import com.atguigu.gulimall.ums.entity.MemberEntity;
import com.atguigu.gulimall.ums.feign.OrderFeignService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 订单支付成功后给用户加积分
 *
 * @author 10017
 */
@Slf4j
@Service
public class MemberRabbitListenerService {

    @Autowired
    private OrderFeignService orderFeignService;

    @Autowired
    private MemberDao memberDao;


    @RabbitListener(queues = {RabbitMQConstant.order_queue_pay_success})
    public void orderPayed(Message message, Channel channel, OrderMqTo orderMqTo) throws IOException {
        try {
            String orderSn = orderMqTo.getOrderSn();
            log.info("获取到支付成功的订单，正在处理订单【{}】积分数据...", orderSn);

            Resp<OrderVo> orderInfo = orderFeignService.getOrderInfo(orderSn);
            OrderVo data = orderInfo.getData();

            Long memberId = data.getMemberId();
            // 获取订单中的订单项集合
            List<OrderItemVo> orderItems = data.getOrderItems();

            Integer grow = 0;
            Integer inter = 0;

            for (OrderItemVo orderItem : orderItems) {
                // 或者再乘以购买的数量，叠加积分
                grow += orderItem.getGiftGrowth();
                inter += orderItem.getGiftIntegration();
            }

            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setId(memberId);
            memberEntity.setGrowth(grow);
            memberEntity.setIntegration(inter);
            memberDao.incrScore(memberEntity);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            log.info("订单积分无数据...");
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }


    }
}
