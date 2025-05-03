package com.example.chatserver.common.auth;

import java.security.Key;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final String secretKey;
    private final int expiration;
    private Key SECRET_KEY;


}
