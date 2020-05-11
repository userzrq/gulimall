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

import com.atguigu.gulimall.sms.entity.SmsCategoryBoundsEntity;
import com.atguigu.gulimall.sms.service.SmsCategoryBoundsService;




/**
 * 商品分类积分设置
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:20:59
 */
@Api(tags = "商品分类积分设置 管理")
@RestController
@RequestMapping("sms/smscategorybounds")
public class SmsCategoryBoundsController {
    @Autowired
    private SmsCategoryBoundsService smsCategoryBoundsService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sms:smscategorybounds:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = smsCategoryBoundsService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sms:smscategorybounds:info')")
    public Resp<SmsCategoryBoundsEntity> info(@PathVariable("id") Long id){
		SmsCategoryBoundsEntity smsCategoryBounds = smsCategoryBoundsService.getById(id);

        return Resp.ok(smsCategoryBounds);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sms:smscategorybounds:save')")
    public Resp<Object> save(@RequestBody SmsCategoryBoundsEntity smsCategoryBounds){
		smsCategoryBoundsService.save(smsCategoryBounds);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sms:smscategorybounds:update')")
    public Resp<Object> update(@RequestBody SmsCategoryBoundsEntity smsCategoryBounds){
		smsCategoryBoundsService.updateById(smsCategoryBounds);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sms:smscategorybounds:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		smsCategoryBoundsService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
