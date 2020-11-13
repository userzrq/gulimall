package com.atguigu.gulimall.ums.dao;

import com.atguigu.gulimall.ums.entity.MemberReceiveAddressEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 会员收货地址
 * 
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-18 10:35:30
 */
@Mapper
@Repository
public interface MemberReceiveAddressDao extends BaseMapper<MemberReceiveAddressEntity> {
	
}
