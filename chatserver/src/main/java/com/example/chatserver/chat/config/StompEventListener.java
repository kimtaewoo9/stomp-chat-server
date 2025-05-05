package com.example.chatserver.chat.config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
public class StompEventListener {

    // connect, disconnect 동작이 잘 일어나는지 검증했다고 보면 됨.

    private final Set<String> sessions = ConcurrentHashMap.newKeySet();

    // SessionConnectEvent 객체를 주입 받으면 커넥트 요청이 발생했을때 이 이벤트 리스너 메서드가 실행됨 .
    @EventListener
    public void connectHandle(SessionConnectEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        sessions.add(accessor.getSessionId());
        log.info("connect session id: {}", accessor.getSessionId());
        log.info("total session: {}", sessions.size());
    }

    @EventListener
    public void disconnectHandle(SessionDisconnectEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        sessions.remove(accessor.getSessionId());
        log.info("disconnect session id: {}", accessor.getSessionId());
        log.info("total session: {}", sessions.size());
    }
}
