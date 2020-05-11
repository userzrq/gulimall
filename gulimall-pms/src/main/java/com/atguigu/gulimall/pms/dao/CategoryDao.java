package com.atguigu.gulimall.pms.dao;

import com.atguigu.gulimall.pms.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 11:31:30
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
