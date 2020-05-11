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

import com.atguigu.gulimall.sms.entity.SmsMemberPriceEntity;
import com.atguigu.gulimall.sms.service.SmsMemberPriceService;




/**
 * 商品会员价格
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:20:59
 */
@Api(tags = "商品会员价格 管理")
@RestController
@RequestMapping("sms/smsmemberprice")
public class SmsMemberPriceController {
    @Autowired
    private SmsMemberPriceService smsMemberPriceService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sms:smsmemberprice:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = smsMemberPriceService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sms:smsmemberprice:info')")
    public Resp<SmsMemberPriceEntity> info(@PathVariable("id") Long id){
		SmsMemberPriceEntity smsMemberPrice = smsMemberPriceService.getById(id);

        return Resp.ok(smsMemberPrice);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sms:smsmemberprice:save')")
    public Resp<Object> save(@RequestBody SmsMemberPriceEntity smsMemberPrice){
		smsMemberPriceService.save(smsMemberPrice);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sms:smsmemberprice:update')")
    public Resp<Object> update(@RequestBody SmsMemberPriceEntity smsMemberPrice){
		smsMemberPriceService.updateById(smsMemberPrice);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sms:smsmemberprice:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		smsMemberPriceService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
