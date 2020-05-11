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

import com.atguigu.gulimall.ums.entity.UmsMemberLoginLogEntity;
import com.atguigu.gulimall.ums.service.UmsMemberLoginLogService;




/**
 * 会员登录记录
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:24:07
 */
@Api(tags = "会员登录记录 管理")
@RestController
@RequestMapping("ums/umsmemberloginlog")
public class UmsMemberLoginLogController {
    @Autowired
    private UmsMemberLoginLogService umsMemberLoginLogService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ums:umsmemberloginlog:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = umsMemberLoginLogService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('ums:umsmemberloginlog:info')")
    public Resp<UmsMemberLoginLogEntity> info(@PathVariable("id") Long id){
		UmsMemberLoginLogEntity umsMemberLoginLog = umsMemberLoginLogService.getById(id);

        return Resp.ok(umsMemberLoginLog);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ums:umsmemberloginlog:save')")
    public Resp<Object> save(@RequestBody UmsMemberLoginLogEntity umsMemberLoginLog){
		umsMemberLoginLogService.save(umsMemberLoginLog);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ums:umsmemberloginlog:update')")
    public Resp<Object> update(@RequestBody UmsMemberLoginLogEntity umsMemberLoginLog){
		umsMemberLoginLogService.updateById(umsMemberLoginLog);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('ums:umsmemberloginlog:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		umsMemberLoginLogService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
