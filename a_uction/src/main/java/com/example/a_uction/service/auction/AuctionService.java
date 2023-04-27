package com.example.a_uction.service.auction;

import static com.example.a_uction.exception.constants.ErrorCode.AUCTION_NOT_FOUND;
import static com.example.a_uction.exception.constants.ErrorCode.BEFORE_START_TIME;
import static com.example.a_uction.exception.constants.ErrorCode.BIDDING_NOT_FOUND;
import static com.example.a_uction.exception.constants.ErrorCode.END_TIME_EARLIER_THAN_START_TIME;
import static com.example.a_uction.exception.constants.ErrorCode.NOT_FOUND_AUCTION_LIST;
import static com.example.a_uction.exception.constants.ErrorCode.NOT_FOUND_AUCTION_STATUS_LIST;
import static com.example.a_uction.exception.constants.ErrorCode.UNABLE_DELETE_AUCTION;
import static com.example.a_uction.exception.constants.ErrorCode.UNABLE_UPDATE_AUCTION;
import static com.example.a_uction.exception.constants.ErrorCode.USER_NOT_FOUND;
import static com.example.a_uction.model.auction.constants.AuctionStatus.COMPLETED;
import static com.example.a_uction.model.auction.constants.AuctionStatus.PROCEEDING;
import static com.example.a_uction.model.auction.constants.AuctionStatus.SCHEDULED;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.auction.constants.AuctionStatus;
import com.example.a_uction.model.auction.constants.TransactionStatus;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.model.auction.dto.AuctionDto.Response;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import com.example.a_uction.model.auctionTransactionHistory.entity.AuctionTransactionHistoryEntity;
import com.example.a_uction.model.auctionTransactionHistory.repository.AuctionTransactionHistoryRepository;
import com.example.a_uction.model.biddingHistory.entity.BiddingHistoryEntity;
import com.example.a_uction.model.biddingHistory.repository.BiddingHistoryRepository;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AuctionService {

	private final AuctionRepository auctionRepository;
	private final UserRepository userRepository;
	private final AuctionFileService auctionFileService;
	private final BiddingHistoryRepository biddingHistoryRepository;
	private final AuctionTransactionHistoryRepository auctionTransactionHistoryRepository;

	private UserEntity getUser(String userEmail) {
		return userRepository.getByUserEmail(userEmail);
	}

	public AuctionDto.Response addAuction(AuctionDto.Request auction, List<MultipartFile> files,
		String userEmail) {

		timeCheck(auction.getStartDateTime(), auction.getEndDateTime());

		auction.setFiles(files);
		// 추가 (파일 업로드 및 저장)
		AuctionEntity auctionEntity =
			auctionFileService.addFiles(auction.getFiles(), auction.toEntity(getUser(userEmail)));

		return AuctionDto.Response.fromEntity(
			auctionRepository.save(auctionEntity));

	}

	public AuctionDto.Response deleteAuction(Long auctionId, String userEmail) {
		AuctionEntity auction = auctionRepository.findByUserIdAndAuctionId(
				getUser(userEmail).getId(), auctionId)
			.orElseThrow(() -> new AuctionException(AUCTION_NOT_FOUND));

		validateModify(auction.getStartDateTime(), "delete");

		// 추가
		auctionFileService.deleteS3Files(auctionId);
		auctionRepository.delete(auction);

		return AuctionDto.Response.fromEntity(auction);
	}

	public AuctionDto.Response getAuctionByAuctionId(Long auctionId) {
		AuctionEntity auctionEntity = auctionRepository.findById(auctionId)
			.orElseThrow(() -> new AuctionException(ErrorCode.AUCTION_NOT_FOUND));

		if ((auctionEntity.getEndDateTime().isBefore(LocalDateTime.now()) ||
			LocalDateTime.now()
				.equals(auctionEntity.getEndDateTime())) && auctionEntity.getBuyerId() == null) {

			auctionFinished(auctionEntity);
		}

		return AuctionDto.Response.fromEntity(auctionEntity);
	}

	public AuctionDto.Response updateAuction(AuctionDto.Request updateAuction,
		List<MultipartFile> files, String userEmail,
		Long auctionId) {
		AuctionEntity auction = auctionRepository.findByUserIdAndAuctionId(
				getUser(userEmail).getId(), auctionId)
			.orElseThrow(() -> new AuctionException(AUCTION_NOT_FOUND));

		validateModify(auction.getStartDateTime(), "update");

		timeCheck(updateAuction.getStartDateTime(), updateAuction.getEndDateTime());

		auction.updateEntity(updateAuction);
		// 추가
		auction = auctionFileService.addFiles(files, auction);

		return AuctionDto.Response.fromEntity(auctionRepository.save(auction));
	}

	public AuctionDto.Response addAuctionImage(MultipartFile file, String userEmail,
		Long auctionId) {
		AuctionEntity auction = auctionRepository.findByUserIdAndAuctionId(
				getUser(userEmail).getId(), auctionId)
			.orElseThrow(() -> new AuctionException(AUCTION_NOT_FOUND));

		auction = auctionFileService.addImage(file, auction);
		return AuctionDto.Response.fromEntity(auction);
	}

	public AuctionDto.Response deleteAuctionImage(String fileUrl, String userEmail,
		Long auctionId) {
		AuctionEntity auction = auctionRepository.findByUserIdAndAuctionId(
				getUser(userEmail).getId(), auctionId)
			.orElseThrow(() -> new AuctionException(AUCTION_NOT_FOUND));

		auction = auctionFileService.deleteImage(fileUrl, auction);
		return AuctionDto.Response.fromEntity(auction);
	}

	public Page<AuctionDto.Response> getAllAuctionListByUserEmail(String userEmail,
		Pageable pageable) {
		var auctionList = auctionRepository.findByUserId(getUser(userEmail).getId(), pageable);
		if (auctionList.isEmpty()) {
			throw new AuctionException(NOT_FOUND_AUCTION_LIST);
		}

		return auctionList.map(Response::fromEntity);
	}

	public Page<AuctionDto.Response> getAllAuctionListByStatus(AuctionStatus status,
		Pageable pageable) {
		Page<AuctionEntity> auctionEntities = null;

		if (status == null || status.equals(PROCEEDING)) {
			auctionEntities = auctionRepository.findByStartDateTimeBeforeAndEndDateTimeAfter(
				LocalDateTime.now(), LocalDateTime.now(), pageable);
		} else if (status.equals(SCHEDULED)) {
			auctionEntities = auctionRepository.findByStartDateTimeAfter(LocalDateTime.now(),
				pageable);
		} else if (status.equals(COMPLETED)) {
			auctionEntities = auctionRepository.findByEndDateTimeBefore(LocalDateTime.now(),
				pageable);
		}

		if (auctionEntities.isEmpty()) {
			throw new AuctionException(NOT_FOUND_AUCTION_STATUS_LIST);
		}
		return auctionEntities.map(Response::fromEntity);
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

	public AuctionDto.Response auctionFinished(AuctionEntity auction) {

		Long auctionId = auction.getAuctionId();

		if (biddingHistoryRepository.existsByAuctionId(auctionId)) {

			auction.setTransactionStatus(TransactionStatus.TRANSACTION_COMPLETE);

			BiddingHistoryEntity biddingHistory = biddingHistoryRepository.findFirstByAuctionIdOrderByCreatedDateDesc(
					auctionId)
				.orElseThrow(() -> new AuctionException(BIDDING_NOT_FOUND));

			biddingHistory.setBidding_result(true);

			auction.setBuyerId(
				userRepository.findByUserEmail(biddingHistory.getBidderEmail())
					.orElseThrow(() -> new AuctionException(USER_NOT_FOUND))
					.getId());

			String buyerEmail = biddingHistory.getBidderEmail();

			AuctionTransactionHistoryEntity auctionTransactionHistory = AuctionTransactionHistoryEntity.builder()
				.price(biddingHistory.getPrice())
				.itemName(auction.getItemName())
				.buyerEmail(buyerEmail)
				.sellerEmail(auction.getUser().getUserEmail())
				.build();

			List<String> imagesSrc = auction.getImagesSrc();

			if (imagesSrc != null) {
				auctionTransactionHistory.setImageSrc(imagesSrc.get(0));
			}

			auctionTransactionHistoryRepository.save(auctionTransactionHistory);
		} else {
			auction.setTransactionStatus(TransactionStatus.TRANSACTION_FAIL);
		}
		return AuctionDto.Response.fromEntity(auctionRepository.save(auction));
	}
}
