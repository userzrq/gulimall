package com.atguigu.gulimall.pms.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.dao.SpuInfoDao;
import com.atguigu.gulimall.pms.entity.SpuInfoEntity;
import com.atguigu.gulimall.pms.service.SpuInfoService;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryPageByCatId(QueryCondition queryCondition, Integer catId) {
        IPage<SpuInfoEntity> page = null;
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<SpuInfoEntity>();
        if(catId != 0){
            //如果catId为0，则是查全站
            wrapper.eq("catalog_id",catId);
            //WHERE catalog_id = ? AND ( spu_name LIKE ? OR id LIKE ? )
            if(!StringUtils.isEmpty(queryCondition.getKey())){
                wrapper.and(obj -> {
                    obj.like("spu_name",queryCondition.getKey());
                    obj.like("id",queryCondition.getKey());
                    return obj;
                });
            }
        }

        page = this.page(new Query<SpuInfoEntity>().getPage(queryCondition),wrapper);
        return new PageVo(page);
    }

}