package com.atguigu.gulimall.pms.dao;

import com.atguigu.gulimall.pms.entity.CategoryEntity;
import com.atguigu.gulimall.pms.vo.CategoryWithChildrensVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品三级分类
 * 
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 11:31:30
 */
@Repository
public interface CategoryDao extends BaseMapper<CategoryEntity> {

    List<CategoryWithChildrensVo> selectCategoryChildrenWithChildrens(@Param("id") Integer id);
}
