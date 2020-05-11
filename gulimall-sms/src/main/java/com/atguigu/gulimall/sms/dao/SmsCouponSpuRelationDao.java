package com.atguigu.gulimall.sms.dao;

import com.atguigu.gulimall.sms.entity.SmsCouponSpuRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券与产品关联
 * 
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:20:59
 */
@Mapper
public interface SmsCouponSpuRelationDao extends BaseMapper<SmsCouponSpuRelationEntity> {
	
}
