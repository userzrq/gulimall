package com.atguigu.gulimall.wms.service.impl;

import com.atguigu.gulimall.wms.service.WareReleaseService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @author 10017
 */
@Service
public class WareReleaseServiceImpl implements WareReleaseService {


    /**
     * 监听死信队列中过时的锁库存信息
     */
    @RabbitListener(queues = "closeSkuStockQueue")
    public void release() {

    }
}
