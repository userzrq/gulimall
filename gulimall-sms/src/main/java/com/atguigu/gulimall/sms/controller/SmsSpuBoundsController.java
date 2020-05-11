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

import com.atguigu.gulimall.sms.entity.SmsSpuBoundsEntity;
import com.atguigu.gulimall.sms.service.SmsSpuBoundsService;




/**
 * 商品spu积分设置
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:20:59
 */
@Api(tags = "商品spu积分设置 管理")
@RestController
@RequestMapping("sms/smsspubounds")
public class SmsSpuBoundsController {
    @Autowired
    private SmsSpuBoundsService smsSpuBoundsService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sms:smsspubounds:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = smsSpuBoundsService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sms:smsspubounds:info')")
    public Resp<SmsSpuBoundsEntity> info(@PathVariable("id") Long id){
		SmsSpuBoundsEntity smsSpuBounds = smsSpuBoundsService.getById(id);

        return Resp.ok(smsSpuBounds);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sms:smsspubounds:save')")
    public Resp<Object> save(@RequestBody SmsSpuBoundsEntity smsSpuBounds){
		smsSpuBoundsService.save(smsSpuBounds);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sms:smsspubounds:update')")
    public Resp<Object> update(@RequestBody SmsSpuBoundsEntity smsSpuBounds){
		smsSpuBoundsService.updateById(smsSpuBounds);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sms:smsspubounds:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		smsSpuBoundsService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
