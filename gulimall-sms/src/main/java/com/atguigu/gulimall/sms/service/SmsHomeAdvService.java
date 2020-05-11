package com.atguigu.gulimall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.sms.entity.SmsHomeAdvEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 首页轮播广告
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:20:59
 */
public interface SmsHomeAdvService extends IService<SmsHomeAdvEntity> {

    PageVo queryPage(QueryCondition params);
}

