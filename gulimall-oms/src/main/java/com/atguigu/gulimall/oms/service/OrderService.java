package com.atguigu.gulimall.oms.service;

import com.atguigu.gulimall.oms.vo.CartVo;
import com.atguigu.gulimall.oms.vo.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.oms.entity.OrderEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 订单
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-18 10:34:36
 */
public interface OrderService extends IService<OrderEntity> {

    PageVo queryPage(QueryCondition params);

    /**
     * 保存订单与订单项
     * @param orderSubmitVo
     * @return
     */
    OrderEntity createAndSaveOrder(OrderSubmitVo orderSubmitVo);
}

