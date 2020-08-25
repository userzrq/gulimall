package com.atguigu.gulimall.commons.to;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class SkuInfoVo {

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
}
