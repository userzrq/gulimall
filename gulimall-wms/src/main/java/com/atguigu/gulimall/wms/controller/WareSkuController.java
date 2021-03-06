package com.atguigu.gulimall.wms.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.SkuStockVo;
import com.atguigu.gulimall.wms.vo.LockStockVo;
import com.atguigu.gulimall.wms.vo.SkuLockVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.wms.entity.WareSkuEntity;
import com.atguigu.gulimall.wms.service.WareSkuService;


/**
 * 商品库存
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-18 10:36:15
 */
@Api(tags = "商品库存 管理")
@RestController
@RequestMapping("wms/waresku")
public class WareSkuController {

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * log: xxx商品库存锁成功了
     * log: xxx商品库存锁失败了
     *
     * @param skuLockVos
     * @return
     */
    @ApiOperation("验库存并锁库存")
    @PostMapping("/checkAndLock")
    public Resp<LockStockVo> lockAndCheckStock(@RequestBody List<SkuLockVo> skuLockVos) {
        LockStockVo lockStockVo = wareSkuService.lockAndCheckStock(skuLockVos);
        return Resp.ok(lockStockVo);
    }


    //wms/waresku/skus
    @PostMapping("/skus")
    public Resp<List<SkuStockVo>> skuWareInfos(@RequestBody List<Long> skuIds) {

        List<WareSkuEntity> list = wareSkuService.list(new QueryWrapper<WareSkuEntity>().in("sku_id", skuIds));

        ArrayList<SkuStockVo> skuStockVos = new ArrayList<>();
        list.forEach(wareSkuEntity -> {
            SkuStockVo skuStockVo = new SkuStockVo();
            BeanUtils.copyProperties(wareSkuEntity, skuStockVo);
            skuStockVos.add(skuStockVo);
        });

        return Resp.ok(skuStockVos);
    }

    //wms/waresku/sku/1
    @GetMapping("/sku/{skuId}")
    public Resp<List<WareSkuEntity>> skuWareInfos(@PathVariable Long skuId) {

        List<WareSkuEntity> list = wareSkuService.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId));
        return Resp.ok(list);
    }


    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('wms:waresku:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = wareSkuService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('wms:waresku:info')")
    public Resp<WareSkuEntity> info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return Resp.ok(wareSku);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('wms:waresku:save')")
    public Resp<Object> save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('wms:waresku:update')")
    public Resp<Object> update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('wms:waresku:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
