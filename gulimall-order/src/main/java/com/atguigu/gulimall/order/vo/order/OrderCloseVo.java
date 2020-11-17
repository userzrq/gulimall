package com.atguigu.gulimall.order.vo.order;

import lombok.Data;

/**
 * @author 10017
 */
@Data
public class OrderCloseVo {

    /**
     * id
     */
    private Long id;
    /**
     * 订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】
     */
    private Integer status;
}
