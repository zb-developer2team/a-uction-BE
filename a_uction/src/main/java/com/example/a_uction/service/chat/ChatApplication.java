package com.example.a_uction.service.chat;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Service
@RequiredArgsConstructor
public class ChatApplication {

	private final RedisTemplate<String, Integer> chatRedisTemplate;
	private final RedisTemplate<String, Object> redisTemplate;

	private static final String DESTINATION_HEADER = "simpDestination";
	private static final String SESSION_ID_HEADER = "simpSessionId";

	@EventListener(SessionConnectEvent.class)
	public void onConnection (SessionConnectEvent connectEvent) {
		String chatRoomId =
			Objects.requireNonNull(
					connectEvent.getMessage().getHeaders().get(DESTINATION_HEADER))
				.toString()
				.split("/")[2];

		redisTemplate.opsForValue()
			.set(Objects.requireNonNull(
				connectEvent.getMessage().getHeaders().get(SESSION_ID_HEADER)).toString(), chatRoomId);

		int count =
			chatRedisTemplate.opsForValue().get(chatRoomId) != null ? Objects.requireNonNull(
				chatRedisTemplate.opsForValue().get(chatRoomId)) + 1 : 1;

		setData(chatRoomId, count);
	}
	@EventListener(SessionDisconnectEvent.class)
	public void onConnection (SessionDisconnectEvent disconnectEvent) {

		String chatRoomId = String.valueOf(
			redisTemplate.opsForValue().getAndDelete(disconnectEvent.getSessionId()));

		int count = Objects.requireNonNull(
			chatRedisTemplate.opsForValue().get(chatRoomId)) - 1;

		this.setData(chatRoomId, count);
	}
	private void setData(String chatRoomId, int count) {
		if (count < 1) {
			redisTemplate.delete(chatRoomId);
		} else {
			chatRedisTemplate.opsForValue().set(chatRoomId, count);
		}
	}
	public Integer getUsers(String chatRoomId) {
		return chatRedisTemplate.opsForValue().get(chatRoomId);
	}
}
