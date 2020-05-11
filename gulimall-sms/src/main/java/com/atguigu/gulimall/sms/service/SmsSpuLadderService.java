package com.atguigu.gulimall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.sms.entity.SmsSpuLadderEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 商品阶梯价格
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:20:59
 */
public interface SmsSpuLadderService extends IService<SmsSpuLadderEntity> {

    PageVo queryPage(QueryCondition params);
}

