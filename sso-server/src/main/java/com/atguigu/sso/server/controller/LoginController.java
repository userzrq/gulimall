package com.atguigu.sso.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.UUID;

@Controller
public class LoginController {

    @RequestMapping("/login.html")
    public String loginPage(@RequestParam(value = "redirect_url", required = false) String redirectUrl, Model model,
                            @CookieValue(value = "atguigusso", required = false) String token
                            //HttpServletRequest request
    ) {
        if (StringUtils.isEmpty(token)) {
            // 没有人登录过
            if (!Objects.isNull(redirectUrl)) {
                model.addAttribute("url", redirectUrl);
            }
            // redirectUrl不为空就不用再设置了，doLogin方法中已设置过了

            // 响应一个登录页面
            return "login";
        } else {
            // 之前有人登陆过了
            return "redirect:" + redirectUrl + "?atguigusso=" + token;
        }
    }

    @PostMapping("/doLogin")
    public String doLogin(String username,
                          String password,
                          String url,
                          Model model,
                          HttpServletResponse response) {
        // 从页面提交的3个数据
        System.out.println("提交的数据: username=" + username + ",password=" + password + ",url=" + url);

        // 登录认证逻辑
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            // 登陆成功
            // 保存到redis, key=username value =
            String token = UUID.randomUUID().toString().substring(0, 5);

            // 不推荐使用UUID的方式，1.用jwt支持本地验证 2.jwt自带负载信息

            // 在redis中存一份用户信息
            // redisTemplate.opsForValue().set(token,user);

            // cookie的值为token,拿到token可以去redis中查
            Cookie cookie = new Cookie("atguigusso", token);


            // response.addCookie 将cookie保存在自己域名上，redirect_url下还是没cookie
            response.addCookie(cookie);

            return "redirect:" + url + "?atguigusso=" + token;
        }

        model.addAttribute("username", username);
        model.addAttribute("password", password);
        model.addAttribute("url", url);

        // 登录未成长，转发为登录页面，之前的数据还会保存
        // 并且会保持POST的请求方式
        return "forward:/login.html";
    }
}
