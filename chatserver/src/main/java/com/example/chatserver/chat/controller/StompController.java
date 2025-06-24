package com.example.chatserver.chat.controller;

import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.service.ChatService;
import com.example.chatserver.chat.service.RedisPubSubService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

	private final SimpMessageSendingOperations messageTemplate;

	private final ChatService chatService;
	private final RedisPubSubService redisPubSubService;

	@MessageMapping("/{roomId}")
	public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto chatMessageDto)
		throws JsonProcessingException {
		log.info(chatMessageDto.getMessage());
		chatService.saveMessage(roomId, chatMessageDto);
		String senderName = chatService.findMemberNameByEmail(
			chatMessageDto.getSenderEmail());

		chatMessageDto.setRoomId(roomId);
		chatMessageDto.setCreatedAt(LocalDateTime.now());
		chatMessageDto.setSenderName(senderName);
		// messageTemplate.convertAndSend("/topic/" + roomId, chatMessageDto);

		// @MessageMapping 애노테이션이 특정 stomp 토픽에 발행되는 메시지를 받음
		// 그걸 받아서 publish 메서드를 통해 .. redis 에 publish 를 해준다 .
		ObjectMapper objectMapper = new ObjectMapper();
		String message = objectMapper.writeValueAsString(chatMessageDto);
		// redis topic 에다가 메시지를 쏜다 ..
		redisPubSubService.pulish("chat", message);
	}
}
