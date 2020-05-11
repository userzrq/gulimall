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

import com.atguigu.gulimall.sms.entity.SmsSeckillSkuNoticeEntity;
import com.atguigu.gulimall.sms.service.SmsSeckillSkuNoticeService;




/**
 * 秒杀商品通知订阅
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:20:59
 */
@Api(tags = "秒杀商品通知订阅 管理")
@RestController
@RequestMapping("sms/smsseckillskunotice")
public class SmsSeckillSkuNoticeController {
    @Autowired
    private SmsSeckillSkuNoticeService smsSeckillSkuNoticeService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sms:smsseckillskunotice:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = smsSeckillSkuNoticeService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sms:smsseckillskunotice:info')")
    public Resp<SmsSeckillSkuNoticeEntity> info(@PathVariable("id") Long id){
		SmsSeckillSkuNoticeEntity smsSeckillSkuNotice = smsSeckillSkuNoticeService.getById(id);

        return Resp.ok(smsSeckillSkuNotice);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sms:smsseckillskunotice:save')")
    public Resp<Object> save(@RequestBody SmsSeckillSkuNoticeEntity smsSeckillSkuNotice){
		smsSeckillSkuNoticeService.save(smsSeckillSkuNotice);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sms:smsseckillskunotice:update')")
    public Resp<Object> update(@RequestBody SmsSeckillSkuNoticeEntity smsSeckillSkuNotice){
		smsSeckillSkuNoticeService.updateById(smsSeckillSkuNotice);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sms:smsseckillskunotice:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		smsSeckillSkuNoticeService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
