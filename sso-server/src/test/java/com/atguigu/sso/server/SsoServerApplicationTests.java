package com.atguigu.sso.server;

import com.atguigu.sso.server.utils.SSOJwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

//@RunWith(SpringRunner.class)
//@SpringBootTest
class SsoServerApplicationTests {

    private String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidXNlcnpycSIsImVtYWlsIjoidXNlcnpycUBxcS5jb20iLCJ0b2tlbiI6IjEyMzQ1NiJ9.SzKk-GDSeO20TOmYkesEP-oRc25S8UmKhYlzQ06VGhE";

    @Test
    void contextLoads() {
        /**
         * 封装JWT的负载信息
         */
        Map<String, Object> loginUser = new HashMap<>();
        loginUser.put("name", "userzrq");
        loginUser.put("email", "userzrq" + "@qq.com");
        loginUser.put("token", "123456");

        String compact = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, SSOJwtUtils.JWT_RULE)
                .setClaims(loginUser)
                .compact();

        System.out.println("生成的jwt: " + compact);
        // eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidXNlcnpycSIsImVtYWlsIjoidXNlcnpycUBxcS5jb20iLCJ0b2tlbiI6IjEyMzQ1NiJ9.SzKk-GDSeO20TOmYkesEP-oRc25S8UmKhYlzQ06VGhE

        // eyJhbGciOiJIUzI1NiJ9.
        // eyJuYW1lIjoidXNlcnpycSIsImVtYWlsIjoidXNlcnpycUBxcS5jb20iLCJ0b2tlbiI6IjEyMzQ1NiJ9.
        // SzKk-GDSeO20TOmYkesEP-oRc25S8UmKhYlzQ06VGhE
    }

    @Test
    public void checkJwt() {
        // 直接拿秘钥验令牌
        Jwt jwt = Jwts.parser().setSigningKey(SSOJwtUtils.JWT_RULE).parse(this.jwt);

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SSOJwtUtils.JWT_RULE).parseClaimsJws(this.jwt);
        System.out.println("headerClaims: " + claimsJws);

        Header header = jwt.getHeader();
        System.out.println("header " + header); // header {alg=HS256}

        Object body = jwt.getBody();
        System.out.println("body " + body); // body {name=userzrq, email=userzrq@qq.com, token=123456}
    }

}
