package com.example.chatserver.common.auth;

import com.example.chatserver.member.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
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
        this.SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey),
            SignatureAlgorithm.HS512.getJcaName());
    }

    public String createToken(String email, Role role) {
        Claims claims = Jwts.claims().setSubject(email); // claims -> 이거 그냥 페이로드라고 생각하면 된다 .
        claims.put("role", role);
        Date now = new Date();
        String token = Jwts.builder() // Jwts.builder를 사용하면 기본적으로 헤더를 설정해줌.
            .setClaims(claims)
            .setIssuedAt(now) // 토큰 발행 시간 .
            .setExpiration(new Date(now.getTime() + expiration * 60 * 1000L)) // 만료 일자 설정 .
            .signWith(SECRET_KEY)
            .compact();
        return token;
    }
}
