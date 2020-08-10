package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.pms.entity.SkuImagesEntity;
import com.atguigu.gulimall.pms.entity.SkuInfoEntity;
import com.atguigu.gulimall.pms.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.pms.service.ItemService;
import com.atguigu.gulimall.pms.service.SkuImagesService;
import com.atguigu.gulimall.pms.service.SkuInfoService;
import com.atguigu.gulimall.pms.service.SpuInfoDescService;
import com.atguigu.gulimall.pms.vo.SkuItemDetailVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    @Qualifier("mainThreadPool")
    ThreadPoolExecutor mainThreadPool;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SpuInfoDescService spuInfoDescService;


    @Override
    public SkuItemDetailVo getDetail(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemDetailVo detailVo = new SkuItemDetailVo();

        // 将异步任务交由线程池运行,默认会使用它自身的线程池，但可以配置我们自己的线程池
        //        CompletableFuture.runAsync(() -> {
        //        }, threadPoolExecutor);  // run方法无返回值

        //        CompletableFuture.supplyAsync(); 该方法有返回值

        // 1.当前sku的基本信息 2s
        CompletableFuture<SkuInfoEntity> skuInfo = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuById = skuInfoService.getById(skuId);
            return skuById;
        }, mainThreadPool);

        // thenAccept可以接收参数，thenRun不能接收参数
        // thenAcceptAsync 异步提交给线程池，跟之前的线程不共有一个线程
        CompletableFuture<Void> skuInfoLater = skuInfo.thenAcceptAsync((t) -> {
            BeanUtils.copyProperties(t, detailVo);
        }, mainThreadPool);

        // 2.当前sku的所有图片 1s
        CompletableFuture<List<SkuImagesEntity>> skuImages = CompletableFuture.supplyAsync(() -> {
            List<SkuImagesEntity> ImagesById = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
            return ImagesById;
        }, mainThreadPool);

        CompletableFuture<Void> skuImagesLater = skuImages.thenAcceptAsync((t) -> {
            List<String> images = new ArrayList<>(t.size());
            t.forEach((entity) -> {
                images.add(entity.getImgUrl());
            });
            detailVo.setPics(images);
        }, mainThreadPool);

        // 3.当前sku的所有优惠促销信息 2s

        // 4.当前sku的所有可供选择的销售属性组合    2s

        // 5.spu的所有基本属性 1s

        // 6.spu详情介绍    1s
        CompletableFuture<Void> spuDesc = skuInfo.thenAcceptAsync((skuInfoEntity) -> {
            Long spuId = skuInfoEntity.getSpuId();
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
            detailVo.setDesc(spuInfoDescEntity);
        }, mainThreadPool);

        // 按照同步的代码，结果返回至少要在9s后
        //  1.异步，线程，线程池
        //  2.缓存

        CompletableFuture<Void> future = CompletableFuture.allOf(skuInfo, skuInfoLater, skuImages, skuImagesLater, spuDesc);
        // 阻塞住
        future.get();
        // 3.多线程，多核CPU，本质上不是提升了速度，而是提高了并行

        return detailVo;
    }
}
