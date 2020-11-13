package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 封装订单提交上来的信息
 */
@Data
public class OrderSubmitVo {

    /**
     * 订单确认时的订单token
     */
    private String orderToken;

    /**
     * 订单的备注
     */
    private String remark;

    /**
     * 选中的地址id
     */
    private Long addressId;

    /**
     * 提交的一个订单总额
     */
    private BigDecimal totalPrice;

    /**
     * 支付方式 0-在线付款 1-货到付款
     */
    private Integer payType;


    /**
     * 我们购买的商品可以不提交，去购物车中找到勾选中的商品，即需要购买的商品
     */
}
