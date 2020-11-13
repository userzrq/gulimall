package com.atguigu.gulimall.oms.service.impl;

import com.atguigu.gulimall.oms.dao.OrderItemDao;
import com.atguigu.gulimall.oms.entity.OrderItemEntity;
import com.atguigu.gulimall.oms.vo.CartItemVo;
import com.atguigu.gulimall.oms.vo.CartVo;
import com.atguigu.gulimall.oms.vo.OrderSubmitVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.oms.dao.OrderDao;
import com.atguigu.gulimall.oms.entity.OrderEntity;
import com.atguigu.gulimall.oms.service.OrderService;


@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public OrderEntity createAndSaveOrder(OrderSubmitVo orderSubmitVo) {
        CartVo cartVo = orderSubmitVo.getCartVo();
        List<CartItemVo> items = cartVo.getItems();

        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(orderSubmitVo, orderEntity);

        // 订单号
        orderEntity.setOrderSn(IdWorker.getTimeId());
        // 价格信息
        orderEntity.setTotalAmount(cartVo.getTotalPrice());
        orderEntity.setPayAmount(cartVo.getCartPrice());
        orderEntity.setPromotionAmount(cartVo.getReductionPrice());

        orderDao.insert(orderEntity);

        // 订单中的订单项
        items.forEach((itemVo) -> {
            OrderItemEntity itemEntity = new OrderItemEntity();

            // 关联订单Id 和 订单号
            itemEntity.setOrderId(orderEntity.getId());
            itemEntity.setOrderSn(orderEntity.getOrderSn());

            //
            itemEntity.setSkuId(itemVo.getSkuId());
            itemEntity.setSkuName(itemVo.getSkuTitle());
            itemEntity.setSkuPrice(itemVo.getPrice());
            itemEntity.setSkuQuantity(itemVo.getNum());
            int insert = orderItemDao.insert(itemEntity);

            if (insert == 1) {
                log.info("【{}】订单项插入成功,订单项id【{}】", orderEntity.getOrderSn(), itemEntity.getId());
            }
        });


        return orderEntity;
    }

}