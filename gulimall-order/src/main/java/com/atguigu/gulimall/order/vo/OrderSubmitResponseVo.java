package com.atguigu.gulimall.order.vo;

import lombok.Data;

/**
 * 订单提交时返回的响应
 *
 * @author 10017
 */
@Data
public class OrderSubmitResponseVo {

    private String msg;

    private Integer code;
}
