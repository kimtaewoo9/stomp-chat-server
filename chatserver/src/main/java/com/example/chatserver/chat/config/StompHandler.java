package com.example.chatserver.chat.config;

import com.example.chatserver.chat.service.ChatService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    @Value("${jwt.secretKey}")
    private String secretKey;

    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // STOMP 메시지의 헤더에 더 쉽게 접근하기 위해 ..
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == StompCommand.CONNECT) {
            log.info("connect 요청시 토큰 유효성 검증 ..");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String jwtToken = bearerToken.substring(7);

            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwtToken) // signed claim -> 서명된 클레임 정보를 담고 있는 객체 .
                .getBody();
            log.info("token 검증 완료");
        }
        // subscribe 할때도 토큰 보내도록 설정한 다음에 검증 ..
        if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
            log.info("subscribe 요청시 토큰 유효성 검증 ..");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String jwtToken = bearerToken.substring(7);

            Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
            log.info("token 검증 완료");
            // room 에 대한 권한이 있는지 subscribe할때 확인해야함 .
            String email = claims.getSubject(); // subject에서 email 얻을 수 있음 .
            // getDestination 으로 엔드포인트를 가져올 수 있음 .
            // /topic/${this.roomId}
            String roomId = accessor.getDestination().split("/")[2];

            if (!chatService.isParticipant(email, Long.parseLong(roomId))) {
                throw new IllegalArgumentException("permission denied: not a participant");
            }
        }

        return message;
    }
}
