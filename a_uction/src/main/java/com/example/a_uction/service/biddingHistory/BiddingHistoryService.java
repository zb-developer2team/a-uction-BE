package com.example.a_uction.service.biddingHistory;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import com.example.a_uction.model.biddingHistory.dto.BiddingHistoryDto;
import com.example.a_uction.model.biddingHistory.entity.BiddingHistoryEntity;
import com.example.a_uction.model.biddingHistory.repository.BiddingHistoryRepository;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BiddingHistoryService {

    private final BiddingHistoryRepository biddingHistoryRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;

    private UserEntity getUser(String userEmail){
        return userRepository.getByUserEmail(userEmail);
    }

    public int getMaxBiddablePrice(Long auctionId){
        Optional<BiddingHistoryEntity> optionalBiddingHistory = biddingHistoryRepository.findFirstByAuctionIdOrderByCreatedDateDesc(auctionId);
        AuctionEntity auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionException(ErrorCode.AUCTION_NOT_FOUND));

        if (optionalBiddingHistory.isPresent()){
            return optionalBiddingHistory.get().getPrice() + auction.getMinimumBid();
        } else {
            return auction.getStartingPrice();
        }
    }

    private boolean biddingPossible(Long auctionId, Long bidderId, int price){
        AuctionEntity auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionException(ErrorCode.AUCTION_NOT_FOUND));
        Optional<BiddingHistoryEntity> optionalBiddingHistory = biddingHistoryRepository.findFirstByAuctionIdOrderByCreatedDateDesc(auctionId);

        //경매 등록자 본인이 입찰 시도
        if (Objects.equals(auction.getUser().getId(), bidderId))
            throw new AuctionException(ErrorCode.REGISTER_CANNOT_BID);
        if (optionalBiddingHistory.isPresent()){
            if (Objects.equals(optionalBiddingHistory.get().getBidderId(), bidderId)){
                throw new AuctionException(ErrorCode.LAST_BIDDER_SAME);
            }
        }
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
