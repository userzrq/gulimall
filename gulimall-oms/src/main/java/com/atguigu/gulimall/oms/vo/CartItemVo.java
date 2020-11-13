package com.atguigu.gulimall.oms.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于Feign接受的VO对象，属性的计算过程不需要了
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
    /**
     * 购买数量
     */
    private Integer num;
    /**
     * 商品总价
     */
    private BigDecimal totalPrice;
    /**
     * 商品的选中状态
     */
    private boolean check = true;

    /**
     * 商品满减信息
     */
    private List<SkuFullReductionVo> reductions;
    /**
     * 商品优惠券信息
     */
    private List<SkuCouponVo> coupons;
    /**
     * 老价格，第一次加入购物车的价格（用于后期比价）
     */
    private BigDecimal firstPrice;

    private BigDecimal subPrice;
}
