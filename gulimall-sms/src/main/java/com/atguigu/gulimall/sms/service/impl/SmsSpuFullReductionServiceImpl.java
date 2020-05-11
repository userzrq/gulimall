package com.atguigu.gulimall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.sms.dao.SmsSpuFullReductionDao;
import com.atguigu.gulimall.sms.entity.SmsSpuFullReductionEntity;
import com.atguigu.gulimall.sms.service.SmsSpuFullReductionService;


@Service("smsSpuFullReductionService")
public class SmsSpuFullReductionServiceImpl extends ServiceImpl<SmsSpuFullReductionDao, SmsSpuFullReductionEntity> implements SmsSpuFullReductionService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SmsSpuFullReductionEntity> page = this.page(
                new Query<SmsSpuFullReductionEntity>().getPage(params),
                new QueryWrapper<SmsSpuFullReductionEntity>()
        );

        return new PageVo(page);
    }

}