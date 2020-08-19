package com.atguigu.gulimall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物项数据
 */
@Data
public class CartItemVo {

    private Long skuId;

    private String skuTitle;

    /**
     * 商品所在的套餐
     */
    private String setmeal;

    private String pics;

    private BigDecimal price;
    private Integer numl; // 购买数量
    private BigDecimal totalPrice;


    private List<SkuFullReductionVo> reductions; // 商品满减信息

    private List<SkuCouponVo> coupons; // 商品优惠券信息
}
