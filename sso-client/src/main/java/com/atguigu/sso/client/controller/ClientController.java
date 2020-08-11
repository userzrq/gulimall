package com.atguigu.sso.client.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ClientController {

    // ssoserver 的登录页面
    @Value("${sso.server}")
    String ssoserver;

    @GetMapping("/say")
    public String Hello() {
        // String跳转到templates下的thymeleaf页面
        return "hello";
    }

    @GetMapping("/see")
    public String See(@CookieValue(value = "atguigusso", required = false) String ssocookie,
                      HttpServletRequest request) {

        // 只要有关键cookie就认为登录了,没有获取到就是未登录
        if (StringUtils.isEmpty(ssocookie)) {

            // Referer请求头代表我是从哪来的，不代表我是哪
            request.getHeader("Referer");

            String requestURI = request.getRequestURI();        // 资源标识符，是相对的
            StringBuffer requestURL = request.getRequestURL();  // 资源定位符，全地址

            // 重定向到登录页面并跳转页面也携带上
            return "redirect:" + ssoserver + "redirect_url=" + requestURL.toString();
        } else {
            // 登录了
            return "beauty";
        }


    }
}
