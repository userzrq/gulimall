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

import com.atguigu.gulimall.ums.entity.UmsMemberStatisticsInfoEntity;
import com.atguigu.gulimall.ums.service.UmsMemberStatisticsInfoService;




/**
 * 会员统计信息
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:24:07
 */
@Api(tags = "会员统计信息 管理")
@RestController
@RequestMapping("ums/umsmemberstatisticsinfo")
public class UmsMemberStatisticsInfoController {
    @Autowired
    private UmsMemberStatisticsInfoService umsMemberStatisticsInfoService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ums:umsmemberstatisticsinfo:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = umsMemberStatisticsInfoService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('ums:umsmemberstatisticsinfo:info')")
    public Resp<UmsMemberStatisticsInfoEntity> info(@PathVariable("id") Long id){
		UmsMemberStatisticsInfoEntity umsMemberStatisticsInfo = umsMemberStatisticsInfoService.getById(id);

        return Resp.ok(umsMemberStatisticsInfo);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ums:umsmemberstatisticsinfo:save')")
    public Resp<Object> save(@RequestBody UmsMemberStatisticsInfoEntity umsMemberStatisticsInfo){
		umsMemberStatisticsInfoService.save(umsMemberStatisticsInfo);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ums:umsmemberstatisticsinfo:update')")
    public Resp<Object> update(@RequestBody UmsMemberStatisticsInfoEntity umsMemberStatisticsInfo){
		umsMemberStatisticsInfoService.updateById(umsMemberStatisticsInfo);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('ums:umsmemberstatisticsinfo:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		umsMemberStatisticsInfoService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
