package com.atguigu.gulimall.sms.dao;

import com.atguigu.gulimall.sms.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 * 
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-18 10:26:20
 */
@Mapper
public interface MemberPriceDao extends BaseMapper<MemberPriceEntity> {
	
}
