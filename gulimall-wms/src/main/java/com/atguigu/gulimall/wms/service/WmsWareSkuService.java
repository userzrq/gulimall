package com.atguigu.gulimall.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.wms.entity.WmsWareSkuEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 商品库存
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:27:27
 */
public interface WmsWareSkuService extends IService<WmsWareSkuEntity> {

    PageVo queryPage(QueryCondition params);
}

