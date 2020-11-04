package com.atguigu.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于Feign接受的VO对象，属性的计算过程不需要了
 */
@Data
public class CartVo {

    /**
     * 总商品量
     */
    private Integer total;

    /**
     * 总商品价格
     */
    private BigDecimal totalPrice;

    /**
     * 优惠减去的价格
     */
    private BigDecimal reductionPrice;

    /**
     * 购物车应该支付的价格
     */
    private BigDecimal cartPrice;

    /**
     * 关键项,购物车中所有的购物项
     * ---------------------------------------------
     */
    @Getter
    @Setter
    private List<CartItemVo> items;

    /**
     * 临时用户key
     */
    @Getter
    @Setter
    private String userKey;
}
