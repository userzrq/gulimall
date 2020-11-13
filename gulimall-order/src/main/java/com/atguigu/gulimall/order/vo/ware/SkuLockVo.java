package com.atguigu.gulimall.order.vo.ware;

import lombok.Data;

/**
 * @author userzrq
 */
@Data
public class SkuLockVo {
    private Long skuId;
    private Integer num;

    private String orderToken;

    public SkuLockVo(Long skuId, Integer num, String orderToken) {
        this.skuId = skuId;
        this.num = num;
        this.orderToken = orderToken;
    }
}
