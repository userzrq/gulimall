package com.atguigu.gulimall.wms.controller;

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

import com.atguigu.gulimall.wms.entity.WmsShAreaEntity;
import com.atguigu.gulimall.wms.service.WmsShAreaService;




/**
 * 全国省市区信息
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:27:28
 */
@Api(tags = "全国省市区信息 管理")
@RestController
@RequestMapping("wms/wmssharea")
public class WmsShAreaController {
    @Autowired
    private WmsShAreaService wmsShAreaService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('wms:wmssharea:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = wmsShAreaService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('wms:wmssharea:info')")
    public Resp<WmsShAreaEntity> info(@PathVariable("id") Integer id){
		WmsShAreaEntity wmsShArea = wmsShAreaService.getById(id);

        return Resp.ok(wmsShArea);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('wms:wmssharea:save')")
    public Resp<Object> save(@RequestBody WmsShAreaEntity wmsShArea){
		wmsShAreaService.save(wmsShArea);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('wms:wmssharea:update')")
    public Resp<Object> update(@RequestBody WmsShAreaEntity wmsShArea){
		wmsShAreaService.updateById(wmsShArea);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('wms:wmssharea:delete')")
    public Resp<Object> delete(@RequestBody Integer[] ids){
		wmsShAreaService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
