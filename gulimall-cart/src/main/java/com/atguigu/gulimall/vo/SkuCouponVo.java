package com.atguigu.gulimall.vo;

import lombok.Data;

import java.math.BigDecimal;


/**
 * 商品可以用的优惠券
 * 目前优惠券只跟SPU有关联
 */
@Data
public class SkuCouponVo {

    private Long skuId; // 商品id

    private Long couponId;  // 优惠券id

    private String desc;    // 优惠券描述

    private BigDecimal amount; // 优惠券的金额
}