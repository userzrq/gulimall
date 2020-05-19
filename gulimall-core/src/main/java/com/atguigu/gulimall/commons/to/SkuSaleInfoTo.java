package com.atguigu.gulimall.commons.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuSaleInfoTo {

    private Long skuId;
    private BigDecimal growBounds;
    private BigDecimal buyBounds;

    //优惠生效情况[1111（四个状态位，从右到左）;
    // 0 - 无优惠，成长积分是否赠送;
    // 1 - 无优惠，购物积分是否赠送;
    // 2 - 有优惠，成长积分是否赠送;
    // 3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]
    private Integer[] work;
    //上面是  积分设置的信息



    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;
    //上面是  阶梯价格的信息

    /**
     * "fullPrice": 0, //满多少
     * "reducePrice": 0, //减多少
     * "fullAddOther": 0, //满减是否可以叠加其他优惠
     */
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;
}
