package com.example.chatserver.chat.controller;

import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompController {

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    private final ChatService chatService;

    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto chatMessageDto) {

        // 자바 객체를 메시지로 변환하여 전송할 수 있게 해줌 . (/topic/roomId로 메시지를 라우팅)
        log.info(chatMessageDto.getMessage());
        chatService.saveMessage(roomId, chatMessageDto);
        simpMessageSendingOperations.convertAndSend("/topic/" + roomId, chatMessageDto);

    }
}
