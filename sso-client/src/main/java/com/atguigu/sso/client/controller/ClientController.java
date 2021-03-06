package com.atguigu.sso.client.controller;


import com.atguigu.sso.client.utils.GuliJwtUtils;
import io.jsonwebtoken.Jwt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
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
                      @RequestParam(value = "atguigusso", required = false) String ssoparam,  // 从ssoserver跳转过来的token参数会跟在?后面
                      HttpServletRequest request,
                      HttpServletResponse response) {

        String requestURI = request.getRequestURI();        // 资源标识符，是相对的
        StringBuffer requestURL = request.getRequestURL();  // 资源定位符，全地址

        try {
            if (StringUtils.isEmpty(ssoparam)) {
                if (StringUtils.isEmpty(ssocookie)) {
                    // 在两处都不含参数时，抛出空指针异常，被捕获后跳转
                    throw new NullPointerException();
                } else {
                    GuliJwtUtils.checkJwt(ssocookie);
                    return "beauty";
                }
            } else {
                GuliJwtUtils.checkJwt(ssoparam);
                // 同名cookie覆盖
                response.addCookie(new Cookie("atguigusso", ssoparam));
                return "beauty";
            }
        } catch (NullPointerException e) {
            return "redirect:" + ssoserver + "?redirect_url=" + requestURL.toString();
        } catch (Exception e) {
            // 令牌认证失败
            System.out.println("令牌认证失败...");
            request.setAttribute("url", "redirect:" + ssoserver + "?redirect_url=" + requestURL.toString());
            return "error";
        }




        // 只要有关键cookie就认为登录了,没有获取到就是未登录
        if (StringUtils.isEmpty(ssoparam)) {
            // 参数位置也没用，那么就是真的没登录
            if (StringUtils.isEmpty(ssocookie)) {
                // Referer请求头代表我是从哪来的，不代表我是哪
                request.getHeader("Referer");

                // 重定向到登录页面并跳转页面也携带上
                return "redirect:" + ssoserver + "?redirect_url=" + requestURL.toString();
            } else {
                // 如果参数位置有，说明是从ssoserver跳转回来的,且在url中携带了JWT令牌
                try {
                    GuliJwtUtils.checkJwt(ssoparam);

                    // 为了下次登录也能通过，将jwt token存储到cookie中,并没有把存储的对象取出来
                    response.addCookie(new Cookie("atguigusso", ssoparam));
                    return "beauty";
                } catch (Exception e) {
                    // 让重新登录或者返回错误页面
                    request.setAttribute("url", ssoserver + "?redirect_url=" + requestURL.toString());
                    return "error";
                }
            }
        } else {
            // 登录了

            // 但是登录了还是有安全隐患，随意伪造一个atguigusso的cookie也可以访问加密内容，因此需要检验
            // redis.get(); 去redis中查是一种方法，
            // 但也可以远程调用ssoserver对cookie的值进行验证 feign , 但是会耗费性能，sso server服务器压力巨大

            // jwt: json web token

            // 验证cookie的合法性,不合法会报错
            try {
                GuliJwtUtils.checkJwt(ssocookie);
                return "beauty";
            } catch (Exception e) {
                // 让重新登录或者返回错误页面
                request.setAttribute("url", ssoserver + "?redirect_url=" + requestURL.toString());
                return "error";
                //return "redirect:" + ssoserver + "?redirect_url=" + requestURL.toString();
            }
        }
    }
}
