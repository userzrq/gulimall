package com.atguigu.gulimall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * SKU满减信息
 */
@Data
public class SkuFullReductionVo {

    private Long id;
    private Long skuId;
    private String desc; // 优惠描述

    /**
     * 满xxx 减xxx元需要用的参数
     * 满 fullPrice 元，减 reducePrice 元
     */
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer addOther;

    /**
     * 满几件打几折需要用的参数
     * 满 fullCount 件，打 discount 折
     */
    private Integer fullCount;
    private Integer discount;


    /**
     * 0 - 打折
     * 1 - 满减
     */
    private Integer type; //满减的类型，是满几件打几折，还是满xxx减xxx元

}
