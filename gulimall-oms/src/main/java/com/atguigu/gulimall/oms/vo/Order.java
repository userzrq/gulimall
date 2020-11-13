package com.atguigu.gulimall.oms.vo;

import lombok.Data;

@Data
public class Order {

    private Long orderId;

    /**
     * 订单状态
     * 0 - 未支付
     * 1 - 支付成功
     * 2 - 尚未支付
     */
    private Integer status;

    private String desc;
}
