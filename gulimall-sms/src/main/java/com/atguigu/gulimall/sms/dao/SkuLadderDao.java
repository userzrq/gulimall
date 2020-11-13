package com.atguigu.gulimall.sms.dao;

import com.atguigu.gulimall.sms.entity.SkuLadderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 商品阶梯价格
 * 
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-18 10:26:20
 */
@Repository
public interface SkuLadderDao extends BaseMapper<SkuLadderEntity> {
	
}
