package com.atguigu.gulimall.order.service.impl;

import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.Order;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public Order createOrder() {

        Order order = new Order();
        order.setOrderId(IdWorker.getId());
        order.setDesc("商品xxxxxx-xxxxx");
        order.setStatus(0);

        return order;
    }
}
