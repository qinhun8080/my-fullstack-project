package com.example.demo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // 密钥，生产环境请放在配置文件中
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // Token 有效期：24小时 (毫秒)
    private static final long EXPIRATION_TIME = 86400000;

    // 生成 Token
    public String generateToken(String username, Integer userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId) // 把用户ID也存进去
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    // 解析 Token 获取用户名
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // 校验 Token 是否有效
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}