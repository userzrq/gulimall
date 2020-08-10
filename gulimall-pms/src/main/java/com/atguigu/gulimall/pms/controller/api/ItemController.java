package com.atguigu.gulimall.pms.controller.api;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.pms.service.ItemService;
import com.atguigu.gulimall.pms.vo.SkuItemDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutionException;

@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/item/{skuId}.html")
    public Resp<SkuItemDetailVo> skuDetails(@PathVariable("skuId") Long skuId) throws ExecutionException, InterruptedException {
        // 获取详情
        SkuItemDetailVo skuItemDetailVo = itemService.getDetail(skuId);

        return Resp.ok(skuItemDetailVo);
    }

    @GetMapping("/test/setcookie")
    public void setCookie(HttpServletResponse response) {

        Cookie cookie = new Cookie("test", "test");
        // cookie有默认的作用路径,将cookie的作用路径放大

        // 域的设置范围
        cookie.setPath("/");
        // 作用的域名，默认是当前请求所在的域名  localhost:7000/test/cookie -> localhost
        cookie.setDomain("user.atguigu.com");   // user.atguigu.com:7000/test/cookie
        // 只能设置本域名和父域名
        cookie.setDomain(".atguigu.com");   // <- 父域名


        // 域的作用范围
        //  1. user.atguigu.com:7000/test/cookie
        //      1.1.设置cookie; user.atguigu.com  .atguigu.com
        //      1.2.访问 看域名当时设置的作用域
        //          user.atguigu.com
        //              访问user.atguigu.com下的所有都会带上cookie
        //              访问api.atguigu,com下的所有都不会带上
        //              访问atguigu,com下的所有都不会带上
        //          .atguigu.com
        //              访问atguigu.com下的所有都会带上cookie
        //              访问api.atguigu,com下的所有都会带上
        //              访问user.atguigu,com下的所有都会带上
        cookie.


        response.addCookie(new Cookie("hello", "userzrq"));
    }
}
