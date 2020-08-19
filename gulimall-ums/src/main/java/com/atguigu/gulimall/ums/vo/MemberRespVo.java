package com.atguigu.gulimall.ums.vo;

import lombok.Data;


/**
 * 登陆成功后，给前端响应的Vo
 */
@Data
public class MemberRespVo {

    private String username;

    private String email;
    /**
     * 头像
     */
    private String hearder;

    private String mobile;
    /**
     * 个性签名
     */
    private String sign;

    private Long levelId;

    //--------------------------以上部分可以以明文形式返回


    /**
     * 前端访问需要的令牌，由JWT生成
     */
    private String token;

    /**
     * token中可以携带用户的id
     */
    // private Long id;
}
