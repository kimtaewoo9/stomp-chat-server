package com.example.chatserver.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // "/connect"로 엔드포인트 설정 (요청을 /connect 로 들어오게 하겠다) + cors 설정 .
        registry.addEndpoint("/connect")
            .setAllowedOrigins("http://localhost:3000")
            .withSockJS(); // ws:// 가 아닌 http:// 엔드포인트를 사용할 수 있게 해주는 sockJS 라이브러리 .
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        WebSocketMessageBrokerConfigurer.super.configureMessageBroker(registry);
    }
}
