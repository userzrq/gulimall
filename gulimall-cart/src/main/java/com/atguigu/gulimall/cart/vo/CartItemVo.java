package com.atguigu.gulimall.cart.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
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
    // 购买数量
    private Integer num;

    //商品总价
    private BigDecimal totalPrice;

    @Getter
    @Setter
    // 商品的选中状态
    private boolean check = true;


    @Getter
    @Setter
    // 商品满减信息
    private List<SkuFullReductionVo> reductions;

    @Getter
    @Setter
    // 商品优惠券信息
    private List<SkuCouponVo> coupons;

    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(num + ""));
    }

    @Getter
    @Setter
    private BigDecimal firstPrice;    // 老价格，第一次加入购物车的价格（用于后期比价）

    @Setter
    private BigDecimal subPrice;

    // 计算差价
    public BigDecimal getSubPrice() {
        BigDecimal subtract = firstPrice.subtract(subPrice);
        double diff = Double.parseDouble(subtract.toString());
        // 求差价的绝对值
        Double abs = Math.abs(diff);

        BigDecimal bigDecimal = new BigDecimal(abs.toString());
        return bigDecimal;
    }

    // 更新时间
    @Getter @Setter
    private Date updateTime = new Date();
}
