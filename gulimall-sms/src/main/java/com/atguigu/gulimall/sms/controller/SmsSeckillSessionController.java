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

import com.atguigu.gulimall.sms.entity.SmsSeckillSessionEntity;
import com.atguigu.gulimall.sms.service.SmsSeckillSessionService;




/**
 * 秒杀活动场次
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:20:59
 */
@Api(tags = "秒杀活动场次 管理")
@RestController
@RequestMapping("sms/smsseckillsession")
public class SmsSeckillSessionController {
    @Autowired
    private SmsSeckillSessionService smsSeckillSessionService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sms:smsseckillsession:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = smsSeckillSessionService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sms:smsseckillsession:info')")
    public Resp<SmsSeckillSessionEntity> info(@PathVariable("id") Long id){
		SmsSeckillSessionEntity smsSeckillSession = smsSeckillSessionService.getById(id);

        return Resp.ok(smsSeckillSession);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sms:smsseckillsession:save')")
    public Resp<Object> save(@RequestBody SmsSeckillSessionEntity smsSeckillSession){
		smsSeckillSessionService.save(smsSeckillSession);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sms:smsseckillsession:update')")
    public Resp<Object> update(@RequestBody SmsSeckillSessionEntity smsSeckillSession){
		smsSeckillSessionService.updateById(smsSeckillSession);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sms:smsseckillsession:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		smsSeckillSessionService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
