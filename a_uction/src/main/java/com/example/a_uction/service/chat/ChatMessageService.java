package com.example.a_uction.service.chat;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.chat.constants.MessageType;
import com.example.a_uction.model.chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
	private static final String ENTER_MESSAGE = "님이 입장하셨습니다.";

	private final SimpMessageSendingOperations simpMessageSendingOperations;

	public void sendChatMessage(ChatMessage chatMessage) {

		if (chatMessage.getMessageType().equals(MessageType.ENTER)) {
			chatMessage.setMessage(chatMessage.getSender() + ENTER_MESSAGE);
			simpMessageSendingOperations
				.convertAndSend("/topic/" + chatMessage.getChatRoomId(), chatMessage);
		} else {
			simpMessageSendingOperations
				.convertAndSend("/topic/" + chatMessage.getChatRoomId(), chatMessage);
		}
	}

	public Long bidding(ChatMessage message) {

		Long bidPrice = Long.parseLong(message.getMessage());

		validateBidding(bidPrice);

		simpMessageSendingOperations
			.convertAndSend("/topic/" + message.getChatRoomId(), message);

		return bidPrice;
	}

	private void validateBidding(Long bidPrice) {

		if (1000 > bidPrice) {
			throw new AuctionException(ErrorCode.BEFORE_START_TIME);
		}

	}
}
