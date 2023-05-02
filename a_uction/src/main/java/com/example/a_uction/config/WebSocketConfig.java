package com.example.a_uction.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private static final String TOPIC_DESTINATION_PREFIX = "/topic";

	private final ChatInterceptor interceptor;

	@Value("${rabbitmq.host}")
	private String host;
	@Value("${rabbitmq.virtualHost}")
	private String virtualHost;
	@Value("${websocket.port}")
	private int websocketPort;
	@Value("${rabbitmq.clientId}")
	private String clientId;
	@Value("${rabbitmq.clientPw}")
	private String clientPw;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry
			.addEndpoint("/ws")
			.setAllowedOriginPatterns("*");
//			.withSockJS(); //websocket 을 지원하지 않는 브라우저는 SockJs 를 사용한다는 뜻 적용하면 테스트 불가
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {

		registry.setApplicationDestinationPrefixes("/pub")
			.enableStompBrokerRelay(TOPIC_DESTINATION_PREFIX)
			.setRelayHost(host)
			.setVirtualHost(virtualHost)
			.setRelayPort(websocketPort)
			.setClientLogin(clientId)
			.setClientPasscode(clientPw);
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(interceptor);
		registration.taskExecutor().corePoolSize(3).maxPoolSize(5);
	}

	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
		registration.taskExecutor().corePoolSize(3).maxPoolSize(5);
	}
}
