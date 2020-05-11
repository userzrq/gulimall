package com.atguigu.gulimall.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.wms.entity.WmsShAreaEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 全国省市区信息
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:27:28
 */
public interface WmsShAreaService extends IService<WmsShAreaEntity> {

    PageVo queryPage(QueryCondition params);
}

