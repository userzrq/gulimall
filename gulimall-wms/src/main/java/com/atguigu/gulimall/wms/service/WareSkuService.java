package com.atguigu.gulimall.wms.service;

import com.atguigu.gulimall.wms.vo.LockStockVo;
import com.atguigu.gulimall.wms.vo.SkuLockVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.wms.entity.WareSkuEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * 商品库存
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-18 10:36:15
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageVo queryPage(QueryCondition params);

    /**
     * 库存服务尝试验库存并锁库存，并返回最终结果
     *
     * @param skuIds
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    LockStockVo lockAndCheckStock(List<SkuLockVo> skuIds);
}

