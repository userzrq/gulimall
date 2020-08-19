package com.atguigu.gulimall.commons.exception;

public class UsernameAndPasswordInvalidException extends RuntimeException {

    public UsernameAndPasswordInvalidException() {
        super("账号或密码错误，登陆失败");
    }
}
