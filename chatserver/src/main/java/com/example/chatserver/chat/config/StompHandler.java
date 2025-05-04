package com.example.chatserver.chat.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // STOMP 메시지의 헤더에 더 쉽게 접근하기 위해 ..
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if(accessor.getCommand() == StompCommand.CONNECT){
            log.info("connect 요청시 토큰 유효성 검증 ..");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String jwtToken = bearerToken.substring(7);

            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(jwtToken) // signed claim -> 서명된 클레임 정보를 담고 있는 객체 .
                .getPayload();
            log.info("token 검증 완료");
        }
    }
}
