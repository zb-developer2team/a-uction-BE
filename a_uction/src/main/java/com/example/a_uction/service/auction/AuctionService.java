package com.example.a_uction.service.auction;

import static com.example.a_uction.exception.constants.ErrorCode.AUCTION_NOT_FOUND;
import static com.example.a_uction.exception.constants.ErrorCode.BEFORE_START_TIME;
import static com.example.a_uction.exception.constants.ErrorCode.END_TIME_EARLIER_THAN_START_TIME;
import static com.example.a_uction.exception.constants.ErrorCode.NOT_FOUND_AUCTION_LIST;
import static com.example.a_uction.exception.constants.ErrorCode.UNABLE_DELETE_AUCTION;
import static com.example.a_uction.exception.constants.ErrorCode.UNABLE_UPDATE_AUCTION;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionService {

	private final AuctionRepository auctionRepository;
	private final UserRepository userRepository;

	private UserEntity getUser(String userEmail) {
		return userRepository.getByUserEmail(userEmail);
	}

	public AuctionDto.Response addAuction(AuctionDto.Request auction, String userEmail) {

		timeCheck(auction.getStartDateTime(), auction.getEndDateTime());

		return new AuctionDto.Response().fromEntity(
			auctionRepository.save(auction.toEntity(getUser(userEmail))));
	}

	public AuctionDto.Response deleteAuction(Long auctionId, String userEmail) {
		AuctionEntity auction = auctionRepository.findByUserIdAndAuctionId(
				getUser(userEmail).getId(), auctionId)
			.orElseThrow(() -> new AuctionException(AUCTION_NOT_FOUND));

		validateModify(auction.getStartDateTime(), "delete");

		auctionRepository.delete(auction);
		return new AuctionDto.Response().fromEntity(auction);
	}

	public AuctionDto.Response getAuctionByAuctionId(Long auctionId) {
		AuctionEntity auctionEntity = auctionRepository.findById(auctionId)
			.orElseThrow(() -> new AuctionException(ErrorCode.AUCTION_NOT_FOUND));
		return new AuctionDto.Response().fromEntity(auctionEntity);
	}

	public AuctionDto.Response updateAuction(AuctionDto.Request updateAuction, String userEmail,
		Long auctionId) {
		AuctionEntity auction = auctionRepository.findByUserIdAndAuctionId(
				getUser(userEmail).getId(), auctionId)
			.orElseThrow(() -> new AuctionException(AUCTION_NOT_FOUND));

		validateModify(auction.getStartDateTime(), "update");

		timeCheck(updateAuction.getStartDateTime(), updateAuction.getEndDateTime());

		auction.updateEntity(updateAuction);

		return new AuctionDto.Response().fromEntity(auctionRepository.save(auction));
	}

	public Page<AuctionDto.Response> getAllAuctionListByUserEmail(String userEmail,
		Pageable pageable) {
		var auctionList = auctionRepository.findByUserId(getUser(userEmail).getId(), pageable);
		if (auctionList.isEmpty()) {
			throw new AuctionException(NOT_FOUND_AUCTION_LIST);
		}

		return auctionList.map(m -> new AuctionDto.Response().fromEntity(m));
	}

	private void timeCheck(LocalDateTime startTime, LocalDateTime endTime) {
		if (endTime.isBefore(startTime)) {
			// 경매 종료 시간이 경매 시작 시간보다 이름
			throw new AuctionException(END_TIME_EARLIER_THAN_START_TIME);
		}

		if (startTime.isBefore(LocalDateTime.now())) {
			//경매 시작시간이 등록 시간보다 이름
			throw new AuctionException(BEFORE_START_TIME);
		}
	}

	private void validateModify(LocalDateTime startTime, String status) {
		if (startTime.isBefore(LocalDateTime.now())) {
			if (status.equals("update")) {
				throw new AuctionException(UNABLE_UPDATE_AUCTION);
			} else {
				throw new AuctionException(UNABLE_DELETE_AUCTION);
			}
		}
	}
}
