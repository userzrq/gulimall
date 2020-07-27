package com.atguigu.gulimall.commons.to;

import lombok.Data;

@Data
public class SkuStockVo {

    private Long skuId;

    /**
     * 库存数
     */
    private Integer stock;
}
