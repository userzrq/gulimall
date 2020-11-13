package com.atguigu.gulimall.pms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.dao.AttrDao;
import com.atguigu.gulimall.pms.entity.AttrEntity;
import com.atguigu.gulimall.pms.service.AttrService;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryPageBaseAttrsByCatId(QueryCondition queryCondition, Integer catId) {
        IPage<AttrEntity> page = this.page(
                // 分页条件
                new Query<AttrEntity>().getPage(queryCondition),
                // 查询条件（应用了数据库冗余字段设计）
                new QueryWrapper<AttrEntity>()
                        .eq("catelog_id",catId)
                        .eq("attr_type",1)
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryPageSaleAttrsByCatId(QueryCondition queryCondition, Integer catId) {
        IPage<AttrEntity> page = this.page(
                // 分页条件
                new Query<AttrEntity>().getPage(queryCondition),
                // 查询条件（应用了数据库冗余字段设计）
                new QueryWrapper<AttrEntity>()
                        .eq("catelog_id",catId)
                        .eq("attr_type",0)
        );

        return new PageVo(page);
    }

}