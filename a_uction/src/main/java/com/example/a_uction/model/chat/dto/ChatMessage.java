package com.example.a_uction.model.chat.dto;

import com.example.a_uction.model.chat.constants.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
	private MessageType messageType;
	private String sender;
	private String chatRoomId;
	private String message;
	private Long auctionId;
}
