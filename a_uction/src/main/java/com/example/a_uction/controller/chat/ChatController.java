package com.example.a_uction.controller.chat;

import com.example.a_uction.model.chat.dto.ChatMessage;
import com.example.a_uction.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

	private final ChatMessageService chatMessageService;
	@MessageMapping("/send")
	public void sendMessage(@Payload ChatMessage message) {
		chatMessageService.sendChatMessage(message);
	}

	@MessageMapping("/bid")
	public Long bidding(@Payload ChatMessage message) {
		return chatMessageService.bidding(message);
	}

}
