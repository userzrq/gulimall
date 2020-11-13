package com.atguigu.gulimall.oms.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 封装订单提交上来的信息
 *
 * @author 10017
 */
@Data
public class OrderSubmitVo {

    /**
     * 订单唯一token
     */
    private String orderToken;

    /**
     * 订单的备注
     */
    private String remark;

    /**
     * 选中的地址
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
     * 购物车VO
     */
    private CartVo cartVo;

    /**
     * 下单用户Id
     */
    private Long userId;
    /**
     * 收票人邮箱
     */
    private String billReceiverEmail;
    /**
     * 收货人姓名
     */
    private String receiverName;
    /**
     * 收货人电话
     */
    private String receiverPhone;
    /**
     * 收货人邮编
     */
    private String receiverPostCode;
    /**
     * 省份/直辖市
     */
    private String receiverProvince;
    /**
     * 城市
     */
    private String receiverCity;
    /**
     * 区
     */
    private String receiverRegion;
    /**
     * 详细地址
     */
    private String receiverDetailAddress;


    /**
     * 我们购买的商品可以不提交，去购物车中找到勾选中的商品，即需要购买的商品
     */
}
