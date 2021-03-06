package com.atguigu.gulimall.ums.controller;

import java.util.Arrays;


import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.ums.vo.MemberLoginVo;
import com.atguigu.gulimall.ums.vo.MemberOnlineVo;
import com.atguigu.gulimall.ums.vo.MemberRegisterVo;
import com.atguigu.gulimall.ums.vo.MemberRespVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.ums.entity.MemberEntity;
import com.atguigu.gulimall.ums.service.MemberService;


/**
 * 会员
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-18 10:35:30
 */
@Api(tags = "会员 管理")
@RestController
@RequestMapping("ums/member")
public class MemberController {
    @Autowired
    private MemberService memberService;


    /**
     * 用户登陆接口
     *
     * @param vo
     * @return
     */
    @PostMapping("/login")
    public Resp<Object> login(MemberLoginVo vo) {
        MemberRespVo respVo = memberService.login(vo);

        return Resp.ok(respVo);
    }

    /**
     * 假设jwt设有过期时间，过期后在全局过滤器处验证失败
     *
     * 检查用户是否在线（检查用户可用性）
     *
     * @param userId
     * @return
     */
    @GetMapping("/checkUserAvailability")
    public Resp<MemberOnlineVo> isOnline(@RequestParam Long userId) {
        MemberOnlineVo onlineVo = memberService.checkIsOnline(userId);

        return Resp.ok(onlineVo);
    }


    /**
     * 用户信息注册
     *
     * @param vo
     * @return
     */
    @ApiOperation("用户注册")
    @PostMapping("/regist")
    public Resp<Object> register(MemberRegisterVo vo) {

        try {
            memberService.registerUser(vo);
            return Resp.ok(null).msg("用户注册成功");
        } catch (Exception e) {
            return Resp.fail(null).msg(e.getMessage());
        }


    }


    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ums:member:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = memberService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('ums:member:info')")
    public Resp<MemberEntity> info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return Resp.ok(member);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ums:member:save')")
    public Resp<Object> save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ums:member:update')")
    public Resp<Object> update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('ums:member:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
