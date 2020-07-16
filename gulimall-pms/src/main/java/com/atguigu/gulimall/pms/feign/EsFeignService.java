package com.atguigu.gulimall.pms.feign;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.es.EsSkuVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-search")
public interface EsFeignService {

    @PostMapping("/es/spu/up")
    public Resp<Object> spuUp(@RequestBody EsSkuVo vo);

    @PostMapping("/es/spu/down")
    public Resp<Object> spuDown(@RequestBody EsSkuVo vo);
}
