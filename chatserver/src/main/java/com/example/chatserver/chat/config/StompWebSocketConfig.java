package com.example.chatserver.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 웹 소켓 연결을 맺기 위한 엔드포인트 설정
        registry.addEndpoint("/connect")
            .setAllowedOrigins("http://localhost:3000")
            .withSockJS(); // ws:// 가 아닌 http:// 엔드포인트를 사용할 수 있게 해주는 sockJS 라이브러리 .
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 서버로 메시지를 보낼 때 사용할 접두사를 설정함 .
        // 브로커의 엔드포인트를 정함 여기서는 /publish
        registry.setApplicationDestinationPrefixes("/publish");

        // 클라이언트가 특정 주제를 구독할때는 /topic/.. 형식의 주소를 사용함 .
        registry.enableSimpleBroker("/topic");
    }

    // 클라이언트에서 서버로 들어오는 메시지 채널에 대한 설정담당 . 메시지 -> 클라이언트와 서버간에 교환되는 데이터 패킷.
    // connect disconnect subscribe -> 이런 메시지들을 가로채기 위함 이다 .
    // WebSocket을 통해 클라이언트로부터 서버로 들어오는 모든 STOMP 메시지를 가로채서 처리하기 위함.
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }

}
