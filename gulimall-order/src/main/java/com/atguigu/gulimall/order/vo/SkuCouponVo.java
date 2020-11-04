package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
/**
 * 用于Feign接受的VO对象，属性的计算过程不需要了
 */
public class SkuCouponVo {
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 优惠券id
     */
    private Long couponId;
    /**
     * 优惠券描述
     */
    private String desc;
    /**
     * 优惠券的金额
     */
    private BigDecimal amount;
}
