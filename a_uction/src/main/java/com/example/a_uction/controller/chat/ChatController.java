package com.example.a_uction.controller.chat;

import com.example.a_uction.model.chat.dto.Message;
import com.example.a_uction.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

	private final ChatMessageService chatMessageService;

	@MessageMapping("/chatting")
	public void sendMessage(@Payload Message message) {
		chatMessageService.sendChatMessage(message);
	}

	@MessageMapping("/bidding")
	public Long bidding(@Payload Message message) {
		return chatMessageService.bidding(message);
	}

	@GetMapping("/{chatRoomId}/getConnectedUsers")
	public Integer getUsers(@PathVariable String chatRoomId) {
		return chatMessageService.getUsers(chatRoomId);
	}
}
