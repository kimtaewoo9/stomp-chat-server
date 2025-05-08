package com.example.chatserver.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // CONNECT
        registry.addEndpoint("/connect")
            .setAllowedOrigins("http://localhost:3000")
            .setAllowedOrigins("https://stomp-chat-server.up.railway.app")
            .withSockJS(); // ws:// 가 아닌 http:// 엔드포인트를 사용할 수 있게 해주는 sockJS 라이브러리 .
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // SEND (클라이언트에서 서버로 메시지 보낼때 사용)
        registry.setApplicationDestinationPrefixes("/publish");

        // SUBSCRIBE (구독자들에게 메시지를 보냄)
        registry.enableSimpleBroker("/topic");
    }

    // connect disconnect subscribe -> STOMP 메시지들을 가로 채기 위해 하는 설정
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}
