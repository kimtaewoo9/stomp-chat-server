//package com.example.chatserver.chat.config;
//
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//@Component
//@Slf4j
//public class SimpleWebSocketHandler extends TextWebSocketHandler {
//
//    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        sessions.add(session); // session을 저장해줌 .
//        log.info("connected with: {}", session.getId()); // 식별자는 웹소켓 서버에서 생성해줌
//    }
//
//    @Override
//    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message)
//        throws Exception {
//        String payLoad = message.getPayload().toString();
//
//        for (WebSocketSession client : sessions) {
//            if (client.isOpen()) {
//                client.sendMessage(new TextMessage(payLoad)); // String을 TestMessage로 래핑해서 전송
//            }
//        }
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
//        throws Exception {
//        sessions.remove(session);
//        log.info("disconnected with: {}", session.getId());
//    }
//}
