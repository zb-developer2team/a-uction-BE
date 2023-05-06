package com.example.a_uction.controller.chat;

import com.example.a_uction.model.chat.dto.Message;
import com.example.a_uction.service.chat.ChatMessageService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Headers;
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
	public void sendMessage(@Payload Message message, @Headers Map<String, String> map) {
		chatMessageService.sendChatMessage(message, map.get("simpSessionId"));
	}

	@MessageMapping("/bidding")
	public void bidding(@Payload Message message, @Headers Map<String, String> map ) {
		chatMessageService.bidding(message, map.get("simpSessionId"));
	}

	@GetMapping("/{chatRoomId}/get-connected-users")
	public Integer getUsers(@PathVariable String chatRoomId) {
		return chatMessageService.getUsers(chatRoomId);
	}
}
