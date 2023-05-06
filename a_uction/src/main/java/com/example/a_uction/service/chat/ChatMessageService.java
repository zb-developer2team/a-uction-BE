package com.example.a_uction.service.chat;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import com.example.a_uction.model.biddingHistory.entity.BiddingHistoryEntity;
import com.example.a_uction.model.biddingHistory.repository.BiddingHistoryRepository;
import com.example.a_uction.model.chat.constants.MessageType;
import com.example.a_uction.model.chat.dto.Message;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import com.example.a_uction.security.jwt.JwtProvider;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

	private static final String DESTINATION_PREFIX = "/topic/";
	private static final String JOIN_TO_INFO_PACKAGE = "JoinChatting:";

	private final SimpMessageSendingOperations simpMessageSendingOperations;

	private final BiddingHistoryRepository biddingHistoryRepository;
	private final JwtProvider provider;
	private final RedisTemplate<String, Object> redisTemplate;
	private final UserRepository userRepository;
	private final AuctionRepository auctionRepository;
	private final RedissonClient redissonClient;
	private final ChatApplication chatApplication;

	public void sendChatMessage(Message message, String sessionId) {

		setMessage(message, sessionId);

		simpMessageSendingOperations
			.convertAndSend(DESTINATION_PREFIX + message.getChatRoomId(), message);
	}
	private void setMessage(Message message, String sessionId) {
		String token = (String)redisTemplate.opsForValue().get(sessionId);
		message.setSender(provider.getUserEmail(token));
		String chatRoomId = (String) redisTemplate.opsForValue().get(JOIN_TO_INFO_PACKAGE + sessionId);
		if (Objects.requireNonNull(chatRoomId).contains("chat")) {
			message.setMessageType(MessageType.SEND);
		} else {
			message.setMessageType(MessageType.BIDDING);
		}
		message.setChatRoomId(chatRoomId);
	}

	@Transactional
	public void bidding(Message message, String sessionId) {
		setMessage(message, sessionId);
		Long auctionId = Long.parseLong(message.getChatRoomId().replace("bid", ""));
		String lockKey = "auction_lock:" + auctionId;
		RLock lock = redissonClient.getLock(lockKey);
		try {
			lock.lock(1, TimeUnit.SECONDS);

			int bidPrice = Integer.parseInt(message.getContents());

			createBiddingHistory(message.getSender(), auctionId, bidPrice);

			simpMessageSendingOperations
				.convertAndSend(DESTINATION_PREFIX + message.getChatRoomId(), message);
		} finally {
			lock.unlock();
		}
	}

	public Integer getUsers(String chatRoomId) {
		return chatApplication.getUsers(chatRoomId);
	}

	public int getMaxBiddablePrice(Long auctionId) {
		Optional<BiddingHistoryEntity> optionalBiddingHistory = biddingHistoryRepository.findFirstByAuctionIdOrderByCreatedDateDesc(
			auctionId);
		AuctionEntity auction = auctionRepository.findById(auctionId)
			.orElseThrow(() -> new AuctionException(ErrorCode.AUCTION_NOT_FOUND));

		return optionalBiddingHistory
			.map(biddingHistoryEntity -> biddingHistoryEntity.getPrice() + auction.getMinimumBid())
			.orElseGet(auction::getStartingPrice);
	}

	private UserEntity getUser(String userEmail) {
		return userRepository.getByUserEmail(userEmail);
	}

	private boolean biddingPossible(Long auctionId, Long bidderId, int price) {
		AuctionEntity auction = auctionRepository.findById(auctionId)
			.orElseThrow(() -> new AuctionException(ErrorCode.AUCTION_NOT_FOUND));
		Optional<BiddingHistoryEntity> optionalBiddingHistory = biddingHistoryRepository.findFirstByAuctionIdOrderByCreatedDateDesc(
			auctionId);
		UserEntity user = userRepository.findById(bidderId)
			.orElseThrow(() -> new AuctionException(ErrorCode.USER_NOT_FOUND));
		//경매 종료됌
		if (auction.getEndDateTime().isBefore(LocalDateTime.now())) {

			throw new AuctionException(ErrorCode.AUCTION_FINISHED);
		}
		//경매 등록자 본인이 입찰 시도 - 테스트시 주석처리
		if (Objects.equals(auction.getUser().getId(), bidderId)) {
			throw new AuctionException(ErrorCode.REGISTER_CANNOT_BID);
		}
		if (optionalBiddingHistory.isPresent()) {
			if (Objects.equals(optionalBiddingHistory.get().getBidderEmail(),
				user.getUserEmail())) {
				throw new AuctionException(ErrorCode.LAST_BIDDER_SAME);
			}
		}
		//경매 시작 안함
		if (auction.getStartDateTime().isAfter(LocalDateTime.now())) {
			throw new AuctionException(ErrorCode.AUCTION_NOT_STARTS);
		}
		//입찰 시도 금액이 현재 입찰 금액보다 작거나, 경매 시작 금액보다 작음
		if (price < getMaxBiddablePrice(auctionId)) {
			throw new AuctionException(ErrorCode.NOT_BIDDABLE_PRICE);
		}
		// 입찰 금액보다 소유한 예치금이 작음
		if (price > user.getBalance()) {
			throw new AuctionException(ErrorCode.NOT_ENOUGH_USER_BALANCE);
		}

		return true;
	}

	private void createBiddingHistory(String userEmail, Long auctionId, int price) {
		UserEntity user = getUser(userEmail);
		if (!biddingPossible(auctionId, user.getId(), price)) {
			throw new AuctionException(ErrorCode.UNABLE_CREATE_BID);
		}
		biddingHistoryRepository.save(
			BiddingHistoryEntity.builder()
				.bidderEmail(user.getUserEmail())
				.auctionId(auctionId)
				.price(price)
				.build());
	}
}
