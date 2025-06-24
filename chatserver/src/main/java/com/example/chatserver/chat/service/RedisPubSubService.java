package com.example.chatserver.chat.service;

import com.example.chatserver.chat.dto.ChatMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisPubSubService implements MessageListener {

	// publish 객체 .
	private final StringRedisTemplate stringRedisTemplate;

	private final SimpMessageSendingOperations messageTemplate;

	// publish 객체를 활용해서 메시지를 publishing 할거임 .
	// stomp controller 에서 .. publish 함수를 호출해서 레디스에 메시지를 쏴줌 .
	// 발행을 레디스에게 맡긴다 ..
	public void pulish(String channel, String message) {
		stringRedisTemplate.convertAndSend(channel, message);
	}

	// redis 는 push 방식으로 .. 발행자가 메시지를 보내면 서버가 즉시 모든 구독자에게 메시지를 밀어줌.
	// 메시지를 밀어주면 .이 onMessage 메서드를 통해 그 메시지를 받음 .
	// redis 라는 거대한 방송망을 통해 전달된 메시지를 받아서, 실제 클라이언트들이 귀 기울이고 있는
	// 개별 STOMP 채널로 재분배함 .. 어느 서버에 접속하더라도 모든 사용자가 끊김 없이 메시지를 주고 받을 수 있도록 .
	@Override
	public void onMessage(Message message, byte[] pattern) {
		String payload = new String(message.getBody());
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);
			// 최종적으로 stomp broker 에게 전달함 .
			messageTemplate.convertAndSend("/topic/" + chatMessageDto.getRoomId(),
				chatMessageDto);
		} catch (JsonProcessingException e) {
			log.error("[RedisPubSubService.onMessage] parsing error");
		}

	}
}
