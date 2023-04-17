package com.example.a_uction.service.chat;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.chat.constants.MessageType;
import com.example.a_uction.model.chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import com.example.a_uction.model.biddingHistory.dto.BiddingHistoryDto;
import com.example.a_uction.model.biddingHistory.entity.BiddingHistoryEntity;
import com.example.a_uction.model.biddingHistory.repository.BiddingHistoryRepository;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
	private static final String ENTER_MESSAGE = "님이 입장하셨습니다.";

	private final SimpMessageSendingOperations simpMessageSendingOperations;

	private final BiddingHistoryRepository biddingHistoryRepository;
	private final UserRepository userRepository;
	private final AuctionRepository auctionRepository;
	private final RedissonClient redissonClient;
	int setPrice = 0;
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

	@Transactional
	public int bidding(ChatMessage message) {
		String lockKey = "auction_lock:" + message.getAuctionId();
		RLock lock = redissonClient.getLock(lockKey);
		try {
			lock.lock(1, TimeUnit.SECONDS);

			int bidPrice = Integer.parseInt(message.getMessage());
			BiddingHistoryDto.Request request = BiddingHistoryDto.Request.builder()
					.auctionId(message.getAuctionId())
					.price(bidPrice + setPrice)
					.build();

			setPrice+=1000;
			createBiddingHistory(message.getSender(), request);

			simpMessageSendingOperations
					.convertAndSend("/topic/" + message.getChatRoomId(), message);
		} finally {
			lock.unlock();
		}
		return 0;
	}


	private UserEntity getUser(String userEmail){
		return userRepository.getByUserEmail(userEmail);
	}

	public int getMaxBiddablePrice(Long auctionId){
		Optional<BiddingHistoryEntity> optionalBiddingHistory = biddingHistoryRepository.findFirstByAuctionIdOrderByCreatedDateDesc(auctionId);
		AuctionEntity auction = auctionRepository.findById(auctionId)
				.orElseThrow(() -> new AuctionException(ErrorCode.AUCTION_NOT_FOUND));

		return optionalBiddingHistory
				.map(biddingHistoryEntity -> biddingHistoryEntity.getPrice() + auction.getMinimumBid())
				.orElseGet(auction::getStartingPrice);
	}

	private boolean biddingPossible(Long auctionId, Long bidderId, int price){
		AuctionEntity auction = auctionRepository.findById(auctionId)
				.orElseThrow(() -> new AuctionException(ErrorCode.AUCTION_NOT_FOUND));
		Optional<BiddingHistoryEntity> optionalBiddingHistory = biddingHistoryRepository.findFirstByAuctionIdOrderByCreatedDateDesc(auctionId);

		//경매 등록자 본인이 입찰 시도
//		if (Objects.equals(auction.getUser().getId(), bidderId))
//			throw new AuctionException(ErrorCode.REGISTER_CANNOT_BID);
//		if (optionalBiddingHistory.isPresent()){
//			if (Objects.equals(optionalBiddingHistory.get().getBidderId(), bidderId)){
//				throw new AuctionException(ErrorCode.LAST_BIDDER_SAME);
//			}
//		}
		//경매 시작 안함
		if (auction.getStartDateTime().isAfter(LocalDateTime.now()))
			throw new AuctionException(ErrorCode.AUCTION_NOT_STARTS);
		//경매 종료됌
		if (auction.getEndDateTime().isBefore(LocalDateTime.now()))
			throw new AuctionException(ErrorCode.AUCTION_FINISHED);
		//입찰 시도 금액이 현재 입찰 금액보다 작거나, 경매 시작 금액보다 작음
		if (price < getMaxBiddablePrice(auctionId))
			throw new AuctionException(ErrorCode.NOT_BIDDABLE_PRICE);

		return true;
	}

	public BiddingHistoryDto.Response createBiddingHistory(String userEmail, BiddingHistoryDto.Request request){
		UserEntity user = getUser(userEmail);
		if (!biddingPossible(request.getAuctionId(), user.getId(), request.getPrice())) {
			throw  new AuctionException(ErrorCode.UNABLE_CREATE_BID);
		}
		return new BiddingHistoryDto.Response().fromEntity(biddingHistoryRepository.save(request.toEntity(user)));
	}
}
