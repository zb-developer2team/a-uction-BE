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
	//@BiddingLock
	public int bidding(@Payload ChatMessage message) throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			Thread.sleep(500);
			chatMessageService.bidding(message);
		}
		return 1;
	}

}
