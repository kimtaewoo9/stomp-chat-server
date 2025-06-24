package com.example.chatserver.common.config;

import com.example.chatserver.chat.service.RedisPubSubService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

	@Value("${spring.redis.data.host}")
	private String host;

	@Value("${spring.redis.data.port}")
	private int port;

	// 레디스에 연결하기 위한 연결 객체 .
	@Bean
	@Qualifier("chatPubSub") // 여러 개의 연결 객체를 만들 수 있음 . 또 다른 용도의 Redis 연결을 할때 .. @Qualifier 가 필요함 .
	public RedisConnectionFactory chatPubSubFactory() {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
		configuration.setHostName(host);
		configuration.setPort(port);

		// lettuce -> 고성능 비동기 통신 기능 ..
		return new LettuceConnectionFactory(configuration);
	}

	// publish 객체 .
	// redis 서버와 통신 할 수 있는 사용하기 편리한, 전용도구 상자를 만들어서 spring 이 언제든 꺼 쓸 수 있도록 등록하는 과정 .
	@Bean
	@Qualifier("chatPubSub")
	public StringRedisTemplate stringRedisTemplate(
		@Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory) {
		return new StringRedisTemplate(redisConnectionFactory);
	}

	// subscribe 객체 .
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(
		@Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory,
		MessageListenerAdapter messageListenerAdapter) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		// listener 는 항상 chat 만 바라 보고 있다가 .. chat 이라는 토픽에 메시지가 들어오면 subscribe 함 .
		container.addMessageListener(messageListenerAdapter, new PatternTopic("chat"));

		return container;
	}

	// redis 에서 수신된 메시지를 처리하는 객체 . subscribe 객체가 메시지를 받아서 message listener adapter 에게 던짐 .
	// message listener adapter 는 특정한 클래스를 지정해서, 그 클래스에게 처리하라고 던져줌 .
	// 여기서는 RedisPubSubService 에다가 메시지를 던짐 .
	@Bean
	public MessageListenerAdapter messageListenerAdapter(RedisPubSubService redisPubSubService) {
		// subscribe 가 되면 .. redisPubSubService 안에 onMessage 라는 메서드가 호출이 됨 .
		return new MessageListenerAdapter(redisPubSubService, "onMessage");
	}
}
