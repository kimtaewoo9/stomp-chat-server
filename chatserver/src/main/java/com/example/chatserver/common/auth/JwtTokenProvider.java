package com.example.chatserver.common.auth;

import com.example.chatserver.member.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
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

    public String createToken(String email, String name, Role role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("name", name);
        claims.put("role", role);

        Date now = new Date();
        // Jwts.builder를 사용하면 기본적으로 헤더를 설정해줌.
        return Jwts.builder() // Jwts.builder를 사용하면 기본적으로 헤더를 설정해줌.
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(
                new Date(now.getTime() + expiration * 24 * 60 * 60 * 1000L)) // 기준이 밀리 세컨드임 .
//            .setExpiration(new Date(now.getTime() + 10 * 1000)) // 테스트를 위해 10초로 설정 .
            .signWith(SECRET_KEY)
            .compact();
    }
}
