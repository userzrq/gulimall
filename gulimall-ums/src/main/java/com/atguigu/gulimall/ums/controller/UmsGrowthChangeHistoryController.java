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

import com.atguigu.gulimall.ums.entity.UmsGrowthChangeHistoryEntity;
import com.atguigu.gulimall.ums.service.UmsGrowthChangeHistoryService;




/**
 * 成长值变化历史记录
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:24:07
 */
@Api(tags = "成长值变化历史记录 管理")
@RestController
@RequestMapping("ums/umsgrowthchangehistory")
public class UmsGrowthChangeHistoryController {
    @Autowired
    private UmsGrowthChangeHistoryService umsGrowthChangeHistoryService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ums:umsgrowthchangehistory:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = umsGrowthChangeHistoryService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('ums:umsgrowthchangehistory:info')")
    public Resp<UmsGrowthChangeHistoryEntity> info(@PathVariable("id") Long id){
		UmsGrowthChangeHistoryEntity umsGrowthChangeHistory = umsGrowthChangeHistoryService.getById(id);

        return Resp.ok(umsGrowthChangeHistory);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ums:umsgrowthchangehistory:save')")
    public Resp<Object> save(@RequestBody UmsGrowthChangeHistoryEntity umsGrowthChangeHistory){
		umsGrowthChangeHistoryService.save(umsGrowthChangeHistory);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ums:umsgrowthchangehistory:update')")
    public Resp<Object> update(@RequestBody UmsGrowthChangeHistoryEntity umsGrowthChangeHistory){
		umsGrowthChangeHistoryService.updateById(umsGrowthChangeHistory);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('ums:umsgrowthchangehistory:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		umsGrowthChangeHistoryService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
