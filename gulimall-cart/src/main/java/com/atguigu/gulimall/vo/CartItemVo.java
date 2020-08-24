package com.atguigu.gulimall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物项数据(购物车中每一项的属性)
 */
public class CartItemVo {

    @Getter
    @Setter
    private Long skuId;

    @Getter
    @Setter
    private String skuTitle;

    /**
     * 商品所在的套餐
     */
    @Getter
    @Setter
    private String setmeal;

    @Getter
    @Setter
    private String pics;

    @Getter
    @Setter
    private BigDecimal price;

    @Getter
    @Setter
    private Integer num; // 购买数量

    private BigDecimal totalPrice; //商品总价

    @Getter
    @Setter
    private List<SkuFullReductionVo> reductions; // 商品满减信息

    @Getter
    @Setter
    private List<SkuCouponVo> coupons; // 商品优惠券信息

    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(num + ""));
    }
}
