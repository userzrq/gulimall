package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.pms.entity.SkuImagesEntity;
import com.atguigu.gulimall.pms.service.ItemService;
import com.atguigu.gulimall.pms.service.SkuImagesService;
import com.atguigu.gulimall.pms.vo.SkuItemDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    @Qualifier("mainThreadPool")
    ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    SkuImagesService skuImagesService;


    @Override
    public SkuItemDetailVo getDetail(Long skuId) throws ExecutionException, InterruptedException {

        SkuItemDetailVo detailVo = new SkuItemDetailVo();

        // 1.当前sku的基本信息 2s

        // 将异步任务交由线程池运行,默认会使用它自身的线程池，但可以配置我们自己的线程池
//        CompletableFuture.runAsync(() -> {
//        }, threadPoolExecutor);  // run方法无返回值

        CompletableFuture<SkuImagesEntity> future = CompletableFuture.supplyAsync(() -> {
            SkuImagesEntity imageEntity = skuImagesService.getById(1);
            return imageEntity;
        }, threadPoolExecutor);// supply有返回值

        SkuImagesEntity skuImagesEntity = future.get();
        

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
