package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.util.List;

/**
 * 订单确认页Vo
 */
@Data
public class OrderConfirmVo {

    /**
     * 用户id查找到的所有收货地址 （Feign远程查）
     */
    private List<MemberAddressVo> addresses;

    /**
     * 获取用户在购物车中选中的需要购买的所有商品，以及价格优惠等信息
     */
    private CartVo cartVo;

    /**
     * 获取用户领取的所有商品能用的优惠券
     */

    /**
     * 获取用户的购物可抵扣积分
     */

    /**
     * 交易令牌（防止订单重复提交）
     */
    private String orderToken;
}
