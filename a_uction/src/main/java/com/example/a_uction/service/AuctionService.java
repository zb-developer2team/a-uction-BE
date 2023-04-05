package com.example.a_uction.service;

import com.example.a_uction.model.auction.constants.AuctionStatus;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuctionService {
    private final AuctionRepository auctionRepository;

    public AuctionDto addAuction(AuctionEntity auction){
        if (auction.getEndDateTime().isBefore(auction.getStartDateTime())){
            //TODO 익셉션처리
            // 경매 종료 시간이 경매 시작 시간보다 이름
            throw new RuntimeException();
        }

        if (auction.getStartDateTime().isBefore(LocalDateTime.now())){
            //경매 시작시간이 등록 시간보다 이름
            throw new RuntimeException();
        }
        auction.setAuctionStatus(AuctionStatus.SCHEDULED);
        return new AuctionDto().toDto(auctionRepository.save(auction));
    }
}
