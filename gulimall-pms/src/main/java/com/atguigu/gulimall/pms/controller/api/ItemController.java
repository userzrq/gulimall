package com.atguigu.gulimall.pms.controller.api;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.pms.service.ItemService;
import com.atguigu.gulimall.pms.vo.SkuItemDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/item/{skuId}.html")
    public Resp<SkuItemDetailVo> skuDetails(@PathVariable("skuId") Long skuId) {
        // 获取详情
        SkuItemDetailVo skuItemDetailVo = itemService.getDetail(skuId);

        return Resp.ok(skuItemDetailVo);
    }
}
