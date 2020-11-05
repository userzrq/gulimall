package com.atguigu.gulimall.commons.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Map;

public class GuliJwtUtils {

    /**
     * 自定义秘钥,jwt的最后一部分
     */
    private static String key = "USERZRQ_ATGUIGU";

    private static String bearerPrefix = "Bearer ";

    /**
     * @param payload 自定义的负载内容
     * @param claims  jwt默认支持的属性
     * @return
     */
    public static String buildJwt(Map<String, Object> payload, Claims claims) {

        JwtBuilder builder = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, GuliJwtUtils.key)
                .setClaims(payload);//设置自定义的负载

        if (claims != null) {
            if (claims.getId() != null) {
                builder.setId(claims.getId());
            }
            if (claims.getAudience() != null) {
                builder.setAudience(claims.getAudience());
            }
            if (claims.getExpiration() != null) {
                builder.setExpiration(claims.getExpiration());
            }
            if (claims.getNotBefore() != null) {
                builder.setNotBefore(claims.getNotBefore());
            }
            //xxxxx
        }

        String compact = builder.compact();
        return bearerPrefix + compact;
    }


    public static void checkJwt(String jwt) {
        jwt = jwt.substring(bearerPrefix.length());

        Jwts.parser().setSigningKey(key).parse(jwt);
    }

    /**
     * 获得jwt负载中的数据，同时检查了jwt的合法性
     *
     * @param jwt
     * @return
     */
    public static Map<String, Object> getJwtBody(String jwt) {
        jwt = jwt.substring(bearerPrefix.length());

        return Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
    }
}
