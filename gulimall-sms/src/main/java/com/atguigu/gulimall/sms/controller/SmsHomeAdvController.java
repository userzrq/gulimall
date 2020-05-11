package com.atguigu.gulimall.sms.controller;

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

import com.atguigu.gulimall.sms.entity.SmsHomeAdvEntity;
import com.atguigu.gulimall.sms.service.SmsHomeAdvService;




/**
 * 首页轮播广告
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:20:59
 */
@Api(tags = "首页轮播广告 管理")
@RestController
@RequestMapping("sms/smshomeadv")
public class SmsHomeAdvController {
    @Autowired
    private SmsHomeAdvService smsHomeAdvService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sms:smshomeadv:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = smsHomeAdvService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sms:smshomeadv:info')")
    public Resp<SmsHomeAdvEntity> info(@PathVariable("id") Long id){
		SmsHomeAdvEntity smsHomeAdv = smsHomeAdvService.getById(id);

        return Resp.ok(smsHomeAdv);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sms:smshomeadv:save')")
    public Resp<Object> save(@RequestBody SmsHomeAdvEntity smsHomeAdv){
		smsHomeAdvService.save(smsHomeAdv);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sms:smshomeadv:update')")
    public Resp<Object> update(@RequestBody SmsHomeAdvEntity smsHomeAdv){
		smsHomeAdvService.updateById(smsHomeAdv);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sms:smshomeadv:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		smsHomeAdvService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
