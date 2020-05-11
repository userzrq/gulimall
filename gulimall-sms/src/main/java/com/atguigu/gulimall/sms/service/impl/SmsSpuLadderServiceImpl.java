package com.atguigu.gulimall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.sms.dao.SmsSpuLadderDao;
import com.atguigu.gulimall.sms.entity.SmsSpuLadderEntity;
import com.atguigu.gulimall.sms.service.SmsSpuLadderService;


@Service("smsSpuLadderService")
public class SmsSpuLadderServiceImpl extends ServiceImpl<SmsSpuLadderDao, SmsSpuLadderEntity> implements SmsSpuLadderService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SmsSpuLadderEntity> page = this.page(
                new Query<SmsSpuLadderEntity>().getPage(params),
                new QueryWrapper<SmsSpuLadderEntity>()
        );

        return new PageVo(page);
    }

}