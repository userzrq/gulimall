package com.atguigu.gulimall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 整个购物车
 */
@Data
public class CartVo {

    private Integer total; // 总商品量

    private BigDecimal totalPrice; // 总商品价格

    private BigDecimal reductionPrice; // 优惠减去的价格

    private BigDecimal cartPrice; // 购物车应该支付的价格

    /**
     * 关键项,购物车中所有的购物项
     * ---------------------------------------------
     */
    private List<CartItemVo> items;

}
