package com.example.chatserver.chat.controller;

import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.service.ChatService;
import java.time.LocalDateTime;
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
        log.info(chatMessageDto.getMessage());
        chatService.saveMessage(roomId, chatMessageDto);
        String senderName = chatService.findMemberNameByEmail(
            chatMessageDto.getSenderEmail());

        chatMessageDto.setRoomId(roomId);
        chatMessageDto.setCreatedAt(LocalDateTime.now());
        chatMessageDto.setSenderName(senderName);
        simpMessageSendingOperations.convertAndSend("/topic/" + roomId, chatMessageDto);
    }
}
