package com.atguigu.gulimall.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.wms.entity.WmsFeightTemplateEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 运费模板
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:27:28
 */
public interface WmsFeightTemplateService extends IService<WmsFeightTemplateEntity> {

    PageVo queryPage(QueryCondition params);
}

