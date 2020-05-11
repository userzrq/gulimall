package com.atguigu.gulimall.oms.controller;

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

import com.atguigu.gulimall.oms.entity.OmsOrderSettingEntity;
import com.atguigu.gulimall.oms.service.OmsOrderSettingService;




/**
 * 订单配置信息
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:17:16
 */
@Api(tags = "订单配置信息 管理")
@RestController
@RequestMapping("oms/omsordersetting")
public class OmsOrderSettingController {
    @Autowired
    private OmsOrderSettingService omsOrderSettingService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('oms:omsordersetting:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = omsOrderSettingService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('oms:omsordersetting:info')")
    public Resp<OmsOrderSettingEntity> info(@PathVariable("id") Long id){
		OmsOrderSettingEntity omsOrderSetting = omsOrderSettingService.getById(id);

        return Resp.ok(omsOrderSetting);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('oms:omsordersetting:save')")
    public Resp<Object> save(@RequestBody OmsOrderSettingEntity omsOrderSetting){
		omsOrderSettingService.save(omsOrderSetting);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('oms:omsordersetting:update')")
    public Resp<Object> update(@RequestBody OmsOrderSettingEntity omsOrderSetting){
		omsOrderSettingService.updateById(omsOrderSetting);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('oms:omsordersetting:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		omsOrderSettingService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
