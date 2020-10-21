package com.atguigu.gulimall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.ums.entity.MemberReceiveAddressEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import java.util.List;


/**
 * 会员收货地址
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-18 10:35:30
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    /**
     * 分页查询方法
     * @param params
     * @return
     */
    PageVo queryPage(QueryCondition params);

    /**
     * 获取某个用户的收货地址集合
     * @param memberId
     * @return
     */
    List<MemberReceiveAddressEntity> getAddressesByMemberId(Long memberId);
}

