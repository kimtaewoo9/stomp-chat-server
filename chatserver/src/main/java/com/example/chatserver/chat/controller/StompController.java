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

		// 원래는 @MessageMapping 이 메시지를 받으면 바로 토픽에다가 발행했는데 .
		// 그렇게 하지말고 redis 에 publish 하는거임 .

		// @MessageMapping 애노테이션이 특정 stomp 토픽에 발행되는 메시지를 받음
		// 그걸 받아서 publish 메서드를 통해 .. redis 에 publish 를 해준다 .
		ObjectMapper objectMapper = new ObjectMapper();
		String message = objectMapper.writeValueAsString(chatMessageDto);

		// redis topic 에다가 메시지를 쏜다 ..
		// redis 서버의 chat 이라는 채널에 메시지를 발행함 .
		// 이때 chat 이라는 채널이 만들어짐 .
		redisPubSubService.pulish("chat", message);

		// 메시지의 흐름 ..
		// redisPubSubService.publish 를 통해 메시지 발행함 .
		// stringRedisTemplate 를 통해 레디스의 chat 채널에게 메시지 전달
		// listener 가 듣고 있다가 .adapter 를 통해 해당 메시지를 redisPubSubService 에게 전달.
		// redisPubSubService 의 onMessage 메서드가 stomp broker 에게 전달 .
	}
}
