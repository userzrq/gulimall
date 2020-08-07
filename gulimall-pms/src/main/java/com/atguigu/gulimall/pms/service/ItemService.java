package com.atguigu.gulimall.pms.service;

import com.atguigu.gulimall.pms.vo.SkuItemDetailVo;

/**
 * 商品详情查询功能
 */
public interface ItemService {
    SkuItemDetailVo getDetail(Long skuId);
}
