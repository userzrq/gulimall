package com.atguigu.gulimall.order.service;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.order.vo.Order;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.order.OrderEntityVo;
import com.atguigu.gulimall.order.vo.payment.PayAsyncVo;

import java.util.concurrent.ExecutionException;

public interface OrderService {
    Order createOrder();

    /**
     * 根据用户Id 返回用户确认的订单VO
     *
     * @param userId
     * @return
     */
    OrderConfirmVo confirmOrderData(Long userId);

    /**
     * 提交订单
     *
     * @param orderSubmitVo
     * @param userId
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    Resp<Object> submitOrder(OrderSubmitVo orderSubmitVo, Long userId) throws ExecutionException, InterruptedException;

    /**
     * 接受支付宝的支付回调
     *
     * @param vo
     */
    void paySuccess(PayAsyncVo vo);
}
