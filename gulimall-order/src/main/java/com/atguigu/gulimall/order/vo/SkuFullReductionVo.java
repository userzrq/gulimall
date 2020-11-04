package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用于Feign接受的VO对象，属性的计算过程不需要了
 */
@Data
public class SkuFullReductionVo {
    private Long id;
    private Long skuId;
    /**
     * 优惠描述
     */
    private String desc;

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
    private BigDecimal discount;


    /**
     * 0 - 打折
     * 1 - 满减
     * 满减的类型，是满几件打几折，还是满xxx减xxx元
     */
    private Integer type;
}
