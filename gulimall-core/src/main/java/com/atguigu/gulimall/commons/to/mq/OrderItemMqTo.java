package com.atguigu.gulimall.commons.to.mq;


import lombok.Data;

import java.math.BigDecimal;


/**
 * MQ中传输的订单项信息
 *
 * @author 10017
 */
@Data
public class OrderItemMqTo {

    private Long id;

    private Long orderId;

    private String orderSn;

    private Long skuId;

    private String skuName;

    private String skuPic;

    private BigDecimal skuPrice;

    private Integer skuQuantity;

}
