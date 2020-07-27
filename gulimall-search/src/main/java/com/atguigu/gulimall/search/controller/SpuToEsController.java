package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.es.EsSkuVo;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/es")
public class SpuToEsController {

    @Autowired
    JestClient jestClient;

    /**
     * 商品上架接口
     *
     * @return
     */
    @PostMapping("/spu/up")
    public Resp<Object> spuUp(@RequestBody List<EsSkuVo> vos) {

        vos.forEach(vo -> {
            Index index = new Index.Builder(vo)
                    .index(Constant.ES_SPU_INDEX)
                    .type(Constant.ES_SPU_TYPE)
                    .id(vo.getId().toString()).build();

            try {
                jestClient.execute(index);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        return Resp.ok(null);
    }


    @PostMapping("/spu/down")
    public Resp<Object> spuDown(@RequestBody List<EsSkuVo> vos) {

        return Resp.ok(null);
    }
}
