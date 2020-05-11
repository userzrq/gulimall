package com.atguigu.gulimall.ums.controller;

import java.util.Arrays;
import java.util.Map;


import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;
import com.atguigu.gulimall.commons.bean.Resp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.ums.entity.UmsMemberCollectSpuEntity;
import com.atguigu.gulimall.ums.service.UmsMemberCollectSpuService;




/**
 * 会员收藏的商品
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:24:07
 */
@Api(tags = "会员收藏的商品 管理")
@RestController
@RequestMapping("ums/umsmembercollectspu")
public class UmsMemberCollectSpuController {
    @Autowired
    private UmsMemberCollectSpuService umsMemberCollectSpuService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ums:umsmembercollectspu:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = umsMemberCollectSpuService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('ums:umsmembercollectspu:info')")
    public Resp<UmsMemberCollectSpuEntity> info(@PathVariable("id") Long id){
		UmsMemberCollectSpuEntity umsMemberCollectSpu = umsMemberCollectSpuService.getById(id);

        return Resp.ok(umsMemberCollectSpu);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ums:umsmembercollectspu:save')")
    public Resp<Object> save(@RequestBody UmsMemberCollectSpuEntity umsMemberCollectSpu){
		umsMemberCollectSpuService.save(umsMemberCollectSpu);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ums:umsmembercollectspu:update')")
    public Resp<Object> update(@RequestBody UmsMemberCollectSpuEntity umsMemberCollectSpu){
		umsMemberCollectSpuService.updateById(umsMemberCollectSpu);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('ums:umsmembercollectspu:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		umsMemberCollectSpuService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
