package com.atguigu.gulimall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.pms.entity.AttrGroupEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import java.util.List;


/**
 * 属性分组
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-14 17:42:15
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageVo queryPage(QueryCondition params);


    PageVo queryPageAttrGroupsByCatId(QueryCondition condition,Long catId);
}

