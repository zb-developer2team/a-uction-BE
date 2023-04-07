package com.example.a_uction.service;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.auction.constants.AuctionStatus;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.a_uction.exception.constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuctionService {
    private final AuctionRepository auctionRepository;

    public AuctionDto.Response addAuction(AuctionDto.Request auction){
        if (auction.getEndDateTime().isBefore(auction.getStartDateTime())){
            //TODO 익셉션처리
            // 경매 종료 시간이 경매 시작 시간보다 이트름
            throw new AuctionException(END_TIME_EARLIER_THAN_START_TIME);
        }

        if (auction.getStartDateTime().isBefore(LocalDateTime.now())){
            //경매 시작시간이 등록 시간보다 이름
            throw new AuctionException(BEFORE_START_TIME);
        }
        auction.setAuctionStatus(AuctionStatus.SCHEDULED);
        return new AuctionDto.Response().fromEntity(auctionRepository.save(auction.toEntity()));
    }

    public AuctionDto.Response getAuctionByAuctionId(Long auctionId){
        AuctionEntity auctionEntity = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionException(ErrorCode.AUCTION_NOT_FOUND));
        return new AuctionDto.Response().fromEntity(auctionEntity);
    }

    public AuctionDto.Response updateAuction(AuctionDto.Request updateAuction, int userId, Long auctionId){
        var auction =  auctionRepository.findByUserIdAndAuctionId(userId, auctionId)
                .orElseThrow(() -> new AuctionException(INTERNAL_SERVER_ERROR));

        if (updateAuction.getEndDateTime().isBefore(updateAuction.getStartDateTime())){
            throw new AuctionException(END_TIME_EARLIER_THAN_START_TIME);
        }

        if (updateAuction.getStartDateTime().isBefore(LocalDateTime.now())){
            throw new AuctionException(BEFORE_START_TIME);
        }

        auction.setItemName(updateAuction.getItemName());
        auction.setItemStatus(updateAuction.getItemStatus());
        auction.setStartingPrice(updateAuction.getStartingPrice());
        auction.setMinimumBid(updateAuction.getMinimumBid());
        auction.setStartDateTime(updateAuction.getStartDateTime());
        auction.setEndDateTime(updateAuction.getEndDateTime());
        auction.setItemStatus(updateAuction.getItemStatus());
        //TODO 아직 카테고리 상세 내역을 정하지 않아 추후 확정시 추가 예정
        //auction.setCategory(updateAuction.getCategory());
        auction.setUpdateDateTime(LocalDateTime.now());

        return new AuctionDto.Response().fromEntity(auctionRepository.save(auction));
    }

    public Page<AuctionDto.Response> getAllAuctionListByUserId(int userId, Pageable pageable){
        var auctionList =  auctionRepository.findByUserId(userId, pageable);
        if(auctionList.isEmpty()) throw new AuctionException(NOT_FOUND_AUCTION_LIST);

        return auctionList.map(m -> new AuctionDto.Response().fromEntity(m));
    }

    public Page<AuctionDto.Response> getAuctionListByUserIdAndAuctionStatus(
            int userId, AuctionStatus auctionStatus, Pageable pageable) {

        Page<AuctionEntity> auctionList = auctionRepository.findByUserIdAndAuctionStatus(userId, auctionStatus, pageable);
        if(auctionList.isEmpty()) throw new AuctionException(AUCTION_NOT_FOUND);

        return auctionList.map(m -> new AuctionDto.Response().fromEntity(m));
    }
}
