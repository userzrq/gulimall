package com.atguigu.gulimall.ums.vo;

import lombok.Data;

@Data
public class MemberLoginVo {

    private String loginacct;

    private String password;

    /**
     * 携带验证码，防止高频率请求
     */
    private String code;
}
