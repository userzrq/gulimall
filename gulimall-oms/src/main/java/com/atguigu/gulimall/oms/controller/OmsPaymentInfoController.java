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

import com.atguigu.gulimall.oms.entity.OmsPaymentInfoEntity;
import com.atguigu.gulimall.oms.service.OmsPaymentInfoService;




/**
 * 支付信息表
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:17:16
 */
@Api(tags = "支付信息表 管理")
@RestController
@RequestMapping("oms/omspaymentinfo")
public class OmsPaymentInfoController {
    @Autowired
    private OmsPaymentInfoService omsPaymentInfoService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('oms:omspaymentinfo:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = omsPaymentInfoService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('oms:omspaymentinfo:info')")
    public Resp<OmsPaymentInfoEntity> info(@PathVariable("id") Long id){
		OmsPaymentInfoEntity omsPaymentInfo = omsPaymentInfoService.getById(id);

        return Resp.ok(omsPaymentInfo);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('oms:omspaymentinfo:save')")
    public Resp<Object> save(@RequestBody OmsPaymentInfoEntity omsPaymentInfo){
		omsPaymentInfoService.save(omsPaymentInfo);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('oms:omspaymentinfo:update')")
    public Resp<Object> update(@RequestBody OmsPaymentInfoEntity omsPaymentInfo){
		omsPaymentInfoService.updateById(omsPaymentInfo);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('oms:omspaymentinfo:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		omsPaymentInfoService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
