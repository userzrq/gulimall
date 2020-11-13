package com.atguigu.gulimall.sms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@EnableWebSecurity    开启web安全检查，没有权限的操作会被403禁止
@Configuration
public class SmsSecurity extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //super.configure(http);
        //Spring security放行所有请求
        http.authorizeRequests().antMatchers("/**").permitAll();

        //关闭csrf防重复提交
        http.csrf().disable();

        //http.authorizeRequests().anyRequest().permitAll();
    }
}
