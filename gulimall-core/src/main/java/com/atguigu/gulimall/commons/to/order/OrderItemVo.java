package com.atguigu.gulimall.commons.to.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 10017
 */
@Data
public class OrderItemVo {


    private Long id;

    private Long orderId;

    private String orderSn;

    private Long spuId;

    private String spuName;

    private String spuPic;

    private String spuBrand;

    private Long categoryId;

    private Long skuId;

    private String skuName;

    private String skuPic;

    private BigDecimal skuPrice;

    private Integer skuQuantity;

    // 少传了一个字段，仓库Id，支付成功后要真正扣库存

    private String skuAttrsVals;

    private BigDecimal promotionAmount;

    private BigDecimal couponAmount;

    private BigDecimal integrationAmount;

    private BigDecimal realAmount;

    private Integer giftIntegration;

    private Integer giftGrowth;
}
