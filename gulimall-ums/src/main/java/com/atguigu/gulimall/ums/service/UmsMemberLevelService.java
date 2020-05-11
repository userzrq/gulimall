package com.atguigu.gulimall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.ums.entity.UmsMemberLevelEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 会员等级
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:24:07
 */
public interface UmsMemberLevelService extends IService<UmsMemberLevelEntity> {

    PageVo queryPage(QueryCondition params);
}

