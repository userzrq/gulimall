package com.atguigu.gulimall.wms.service.impl;

import com.atguigu.gulimall.wms.service.WareReleaseService;
import com.atguigu.gulimall.wms.vo.SkuLock;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 库存解锁方法实现类
 *
 * @author 10017
 */
@Service
public class WareReleaseServiceImpl implements WareReleaseService {


    /**
     * 监听死信队列中过时的锁库存信息,收发对象类型一致
     * <p>
     * 最终一致性
     */
    // @RabbitListener(queues = "closeSkuStockQueue")
    @RabbitListener(queues = "releaseStockQueue")
    public void release(List<SkuLock> skuLocks, Channel channel, Message message) {
        skuLocks.forEach(skuLock -> {
            Long skuId = skuLock.getSkuId();
            Long wareId = skuLock.getWareId();
            Integer locked = skuLock.getLocked();
            String orderToken = skuLock.getOrderToken();
            // 能发送并转发到这个队列中的订单 Locked状态都为true
        });
    }
}
