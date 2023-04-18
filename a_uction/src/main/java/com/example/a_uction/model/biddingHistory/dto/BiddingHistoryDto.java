package com.example.a_uction.model.biddingHistory.dto;

import com.example.a_uction.model.biddingHistory.entity.BiddingHistoryEntity;
import com.example.a_uction.model.user.entity.UserEntity;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class BiddingHistoryDto {

    @Getter
    @Setter
    @Builder
    public static class Request{
        @NotNull
        private Long auctionId;
        @Min(value = 100 , message = "최소 시작 금액은 100원 입니다.")
        private int price;

        public BiddingHistoryEntity toEntity(UserEntity user){
            return BiddingHistoryEntity.builder()
                    .auctionId(this.auctionId)
                    .price(this.price)
                    .bidderId(user.getId())
                    .build();
        }
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private Long auctionId;
        private int price;
        private Long bidderId;
        private boolean biddingResult;

        public BiddingHistoryDto.Response fromEntity(BiddingHistoryEntity biddingHistoryEntity){
            return BiddingHistoryDto.Response.builder()
                    .auctionId(biddingHistoryEntity.getAuctionId())
                    .price(biddingHistoryEntity.getPrice())
                    .bidderId(biddingHistoryEntity.getBidderId())
                    .biddingResult(biddingHistoryEntity.isBidding_result())
                    .build();
        }
    }
}
