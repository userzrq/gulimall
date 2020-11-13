package com.atguigu.gulimall.ums.service;

import com.atguigu.gulimall.commons.exception.EmailExistException;
import com.atguigu.gulimall.commons.exception.PhoneExistException;
import com.atguigu.gulimall.commons.exception.UsernameExistException;
import com.atguigu.gulimall.ums.vo.MemberLoginVo;
import com.atguigu.gulimall.ums.vo.MemberRegisterVo;
import com.atguigu.gulimall.ums.vo.MemberRespVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.ums.entity.MemberEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 会员
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-18 10:35:30
 */
public interface MemberService extends IService<MemberEntity> {

    PageVo queryPage(QueryCondition params);

    /**
     * 用户信息注册
     *
     * @param username
     * @param password
     */
    void registerUser(String username, String password);

    /**
     * 用户信息注册
     *
     * @param vo
     * @throws UsernameExistException
     * @throws PhoneExistException
     * @throws EmailExistException
     */
    void registerUser(MemberRegisterVo vo) throws UsernameExistException, PhoneExistException, EmailExistException;

    MemberRespVo login(MemberLoginVo vo);
}

