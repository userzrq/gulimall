package com.atguigu.gulimall.sms.dao;

import com.atguigu.gulimall.sms.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-18 10:26:20
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
