package com.atguigu.gulimall.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.wms.entity.WmsWareInfoEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 仓库信息
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-11 14:27:27
 */
public interface WmsWareInfoService extends IService<WmsWareInfoEntity> {

    PageVo queryPage(QueryCondition params);
}

