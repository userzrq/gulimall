package com.atguigu.gulimall.order.vo.payment;

import lombok.Data;

@Data
public class PayVo {
    private String out_trade_no;
    private String subject;
    private String total_amount;
    private String body;

}
