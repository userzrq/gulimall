package com.atguigu.gulimall.wms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.wms.dao.WmsShAreaDao;
import com.atguigu.gulimall.wms.entity.WmsShAreaEntity;
import com.atguigu.gulimall.wms.service.WmsShAreaService;


@Service("wmsShAreaService")
public class WmsShAreaServiceImpl extends ServiceImpl<WmsShAreaDao, WmsShAreaEntity> implements WmsShAreaService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<WmsShAreaEntity> page = this.page(
                new Query<WmsShAreaEntity>().getPage(params),
                new QueryWrapper<WmsShAreaEntity>()
        );

        return new PageVo(page);
    }

}