package com.atguigu.gulimall.wms.vo;

import lombok.Data;

@Data
public class SkuLock {
    private Long skuId;
    /**
     * 仓库Id
     */
    private Long wareId;
    /**
     * 锁定的库存量
     */
    private Integer locked;
    private Boolean success;
}
