package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.es.EsSkuVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/es")
public class SpuToEsController {

    /**
     * 商品上架接口
     *
     * @return
     */
    @PostMapping("/spu/up")
    public Resp<Object> spuUp(@RequestBody EsSkuVo vo) {

        return null;
    }


    @PostMapping("/spu/down")
    public Resp<Object> spuDown(@RequestBody EsSkuVo vo) {

        return null;
    }
}
