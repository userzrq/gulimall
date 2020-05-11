package com.atguigu.gulimall.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.oms.entity.OmsOrderSettingEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 订单配置信息
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:17:16
 */
public interface OmsOrderSettingService extends IService<OmsOrderSettingEntity> {

    PageVo queryPage(QueryCondition params);
}

