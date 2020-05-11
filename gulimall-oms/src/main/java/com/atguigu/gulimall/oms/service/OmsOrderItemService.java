package com.atguigu.gulimall.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.oms.entity.OmsOrderItemEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 订单项信息
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:17:16
 */
public interface OmsOrderItemService extends IService<OmsOrderItemEntity> {

    PageVo queryPage(QueryCondition params);
}

