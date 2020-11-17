package com.atguigu.gulimall.commons.to.mq;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * MQ中保存的总订单信息
 *
 * @author 10017
 */
@Data
public class OrderMqTo {

    private Long id;

    private Long memberId;

    private String orderSn;

    private Date createTime;

    private String memberUsername;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private List<OrderItemMqTo> orderItems;

}
