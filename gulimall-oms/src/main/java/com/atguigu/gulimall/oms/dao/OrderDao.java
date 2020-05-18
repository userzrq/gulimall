package com.atguigu.gulimall.oms.dao;

import com.atguigu.gulimall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-18 10:34:36
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
