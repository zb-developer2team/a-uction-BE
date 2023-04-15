package com.example.a_uction.config;

import com.example.a_uction.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatInterceptor implements ChannelInterceptor {

	private final JwtProvider provider;

	@Value("${jwt.prefix}")
	private String tokenPrefix;
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		if (accessor.getCommand() == StompCommand.CONNECT) {

			String authorization = String.valueOf(
				accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION));

			String token = authorization.substring(tokenPrefix.length());

			provider.validateToken(token);

			SecurityContextHolder.getContext()
				.setAuthentication(provider.getAuthentication(token));
		}

		return message;
	}
}
