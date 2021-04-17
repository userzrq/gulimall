package com.atguigu.gulimall.wms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.constant.RabbitMQConstant;
import com.atguigu.gulimall.commons.to.mq.OrderMqTo;
import com.atguigu.gulimall.wms.service.WareReleaseService;
import com.atguigu.gulimall.wms.service.WareSkuService;
import com.atguigu.gulimall.wms.vo.SkuLock;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;


/**
 * 在消息队列中监听需要解锁的库存
 *
 * @author 10017
 */
@Slf4j
@Service
public class WareReleaseServiceImpl implements WareReleaseService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private WareSkuService wareSkuService;


    /**
     * 监听死信队列中过时的锁库存信息,收发对象类型一致
     * <p>
     * 最终一致性
     */
    @RabbitListener(queues = {RabbitMQConstant.order_queue_need_release})
    public void release(OrderMqTo orderMqTo, Channel channel, Message message) throws IOException {
        try {
            Long id = orderMqTo.getId();

            // IdWorker生成的orderToken，在锁库存的时候缓存在了redis中
            String orderSn = orderMqTo.getOrderSn();
            String json = redisTemplate.opsForValue().get(Constant.ORDER_STOCK_LOCKED + orderSn);

            /**
             * 感觉有风险，两个json判断都为非空，可以做成原子验证并删除
             */
            // redis缓存被删后，万一mq异常，消息又进队列了，检查一下，然后将这条消息消费掉（防止订单多次解锁）
            if (!StringUtils.isEmpty(json)) {
                log.info("订单【" + orderSn + "】已被关闭，正在解锁库存......");
                List<SkuLock> skuLocks = null;
                JSONObject jsonObject = JSON.parseObject(json);
                // map.get()
                Object locks = jsonObject.get("skuLocks");
                skuLocks = JSON.parseObject(JSON.toJSONString(locks), new TypeReference<List<SkuLock>>() {
                });

                // 解锁库存方法
                wareSkuService.unlockSkuStock(skuLocks, orderSn);
            } else {
                log.info("订单【" + orderSn + "】正在重复扣库存，将它消费掉");
            }

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            log.error("解锁库存出现异常:{}...重新返回队列进行重试...", e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
