package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.pms.service.ItemService;
import com.atguigu.gulimall.pms.vo.SkuItemDetailVo;
import org.springframework.stereotype.Service;


@Service
public class ItemServiceImpl implements ItemService {


    @Override
    public SkuItemDetailVo getDetail(Long skuId) {

        SkuItemDetailVo detailVo = new SkuItemDetailVo();

        // 1.当前sku的基本信息 2s

        // 2.当前sku的所有图片 1s

        // 3.当前sku的所有优惠促销信息 2s

        // 4.当前sku的所有可供选择的销售属性组合    2s

        // 5.spu的所有基本属性 1s

        // 6.spu详情介绍    1s


        // 按照同步的代码，结果返回至少要在9s后
        //  1.异步，线程，线程池
        //  2.缓存


        // 3.多线程，多核CPU，本质上不是提升了速度，而是提高了并行

        return detailVo;
    }
}
