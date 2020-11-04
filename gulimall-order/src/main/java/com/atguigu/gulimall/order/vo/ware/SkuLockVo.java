package com.atguigu.gulimall.order.vo.ware;

import lombok.Data;

/**
 * @author userzrq
 */
@Data
public class SkuLockVo {
    private Long skuId;
    private Integer num;

    public SkuLockVo(Long skuId, Integer num) {
        this.skuId = skuId;
        this.num = num;
    }
}
