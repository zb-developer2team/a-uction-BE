package com.example.a_uction.service.chat;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Service
@RequiredArgsConstructor
public class ChatApplication {

	private final RedisTemplate<String, Integer> chatRedisTemplate;
	private final RedisTemplate<String, Object> redisTemplate;

	private static final String DESTINATION_HEADER = "simpDestination";
	private static final String SESSION_ID_HEADER = "simpSessionId";
	private static final String TOTAL_USERS_COUNT_PACKAGE = "TotalUserCount:";
	private static final String JOIN_TO_INFO_PACKAGE = "JoinChatting:";

	@EventListener(SessionSubscribeEvent.class)
	public void onSubscription(SessionSubscribeEvent event) {
		String chatRoomId =
			Objects.requireNonNull(
					event.getMessage().getHeaders().get(DESTINATION_HEADER))
				.toString()
				.split("/")[2];

		redisTemplate.opsForValue()
			.set(JOIN_TO_INFO_PACKAGE + Objects.requireNonNull(
				event.getMessage().getHeaders().get(SESSION_ID_HEADER)), chatRoomId);

		int count =
			chatRedisTemplate.opsForValue()
				.get(TOTAL_USERS_COUNT_PACKAGE + chatRoomId) != null ? Objects.requireNonNull(
				chatRedisTemplate.opsForValue().get(TOTAL_USERS_COUNT_PACKAGE + chatRoomId)) + 1 : 1;

		setData(chatRoomId, count);
	}
	@EventListener(SessionDisconnectEvent.class)
	public void onDisconnection (SessionDisconnectEvent disconnectEvent) {

		String chatRoomId = String.valueOf(
			redisTemplate.opsForValue()
				.getAndDelete(JOIN_TO_INFO_PACKAGE + disconnectEvent.getSessionId()));

		int count = Objects.requireNonNull(
			chatRedisTemplate.opsForValue().get(TOTAL_USERS_COUNT_PACKAGE + chatRoomId)) - 1;

		this.setData(chatRoomId, count);
	}
	private void setData(String chatRoomId, int count) {
		if (count < 1) {
			 chatRedisTemplate.delete(TOTAL_USERS_COUNT_PACKAGE + chatRoomId);
		} else {
			chatRedisTemplate.opsForValue().set(TOTAL_USERS_COUNT_PACKAGE + chatRoomId, count);
		}
	}
	public Integer getUsers(String chatRoomId) {
		return chatRedisTemplate.opsForValue().get(TOTAL_USERS_COUNT_PACKAGE + chatRoomId);
	}
}
