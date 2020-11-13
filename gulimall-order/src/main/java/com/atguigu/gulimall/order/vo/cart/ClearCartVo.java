package com.atguigu.gulimall.order.vo.cart;

import lombok.Data;

import java.util.List;

/**
 * @author 10017
 */
@Data
public class ClearCartVo {

    private List<Long> skuIds;

    private Long userId;
}
