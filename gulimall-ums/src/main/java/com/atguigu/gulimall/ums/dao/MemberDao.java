package com.atguigu.gulimall.ums.dao;

import com.atguigu.gulimall.ums.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 会员
 * 
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-18 10:35:30
 */
@Repository
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
