package com.example.chatserver.common.auth;

import com.example.chatserver.member.domain.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final String secretKey;

    private final int expiration;

    private Key SECRET_KEY;

    public JwtTokenProvider(@Value("${jwt.secretKey}") String secretKey,
        @Value("${jwt.expiration}") int expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
        this.SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    public String createToken(String email, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", email);
        claims.put("role", role);
        Date now = new Date();

        String token = Jwts.builder() // Jwts.builder를 사용하면 기본적으로 헤더를 설정해줌.
            .claims(claims)
            .issuedAt(now) // 토큰 발행 시간 .
            .expiration(new Date(now.getTime() + expiration * 60 * 1000L)) // 만료 일자 설정 .
            .signWith(SECRET_KEY)
            .compact();
        return token;
    }
}
