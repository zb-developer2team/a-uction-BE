package com.example.a_uction.model.auction.dto;

import com.example.a_uction.model.auction.constants.AuctionStatus;
import com.example.a_uction.model.auction.constants.Category;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.constants.TransactionStatus;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


public class AuctionDto {

    @Getter
    @Setter
    @Builder //테스트
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotNull(message = "상품 이름을 입력하세요")
        private String itemName;
        @NotNull(message = "상품의 상태를 입력하세요")
        private ItemStatus itemStatus;
        @Min(value = 100, message = "최소 시작 금액은 100원 이상입니다.")
        private int startingPrice;
        @Min(value = 100, message = "최소 호가 단위는 100원 이상입니다.")
        private int minimumBid;
        @Enumerated(EnumType.STRING)
        private Category category;
        private AuctionStatus auctionStatus;
        @NotNull(message = "경매 시작 시간을 입력하세요")
        private LocalDateTime startDateTime;
        @NotNull(message = "경매 종료 시간을 입력하세요")
        private LocalDateTime endDateTime;

        public AuctionEntity toEntity(String userEmail){
            return AuctionEntity.builder()
                    .userEmail(userEmail)
                    .itemName(this.itemName)
                    .itemStatus(this.itemStatus)
                    .startingPrice(this.startingPrice)
                    .minimumBid(this.minimumBid)
                    .auctionStatus(this.auctionStatus)
                    .startDateTime(this.startDateTime)
                    .endDateTime(this.endDateTime)
                    .build();
        }

    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private String itemName;
        private ItemStatus itemStatus;
        private TransactionStatus transactionStatus;
        private AuctionStatus auctionStatus;
        private int startingPrice;
        private int minimumBid;
        private Category category;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;


        public AuctionDto.Response fromEntity(AuctionEntity auctionEntity){
            return AuctionDto.Response.builder()
                    .itemName(auctionEntity.getItemName())
                    .itemStatus(auctionEntity.getItemStatus())
                    .auctionStatus(auctionEntity.getAuctionStatus())
                    .transactionStatus(auctionEntity.getTransactionStatus())
                    .startingPrice(auctionEntity.getStartingPrice())
                    .minimumBid(auctionEntity.getMinimumBid())
                    .category(auctionEntity.getCategory())
                    .startDateTime(auctionEntity.getStartDateTime())
                    .endDateTime(auctionEntity.getEndDateTime())
                    .build();
        }
    }


}
