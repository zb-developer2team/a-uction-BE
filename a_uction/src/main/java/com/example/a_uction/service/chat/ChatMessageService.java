package com.example.a_uction.service.chat;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.chat.constants.MessageType;
import com.example.a_uction.model.chat.dto.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
	private static final String ENTER_MESSAGE = "님이 입장하셨습니다.";
	private static final String DESTINATION = "/topic/";

	private final SimpMessageSendingOperations simpMessageSendingOperations;
	private final ChatApplication chatApplication;

	public void sendChatMessage(Message message) {

		if (message.getMessageType().equals(MessageType.ENTER)) {
			message.setContents(message.getSender() + ENTER_MESSAGE);
			simpMessageSendingOperations
				.convertAndSend(DESTINATION + message.getChatRoomId(), message);
		} else {
			simpMessageSendingOperations
				.convertAndSend(DESTINATION + message.getChatRoomId(), message);
		}
	}

	public Long bidding(Message message) {

		Long bidPrice = Long.parseLong(message.getContents());

		validateBidding(bidPrice);

		simpMessageSendingOperations
			.convertAndSend(DESTINATION + message.getChatRoomId(), message);

		return bidPrice;
	}

	public Integer getUsers(String chatRoomId) {
		return chatApplication.getUsers(chatRoomId);
	}

	private void validateBidding(Long bidPrice) {

		if (1000 > bidPrice) {
			throw new AuctionException(ErrorCode.BEFORE_START_TIME);
		}

	}
}
