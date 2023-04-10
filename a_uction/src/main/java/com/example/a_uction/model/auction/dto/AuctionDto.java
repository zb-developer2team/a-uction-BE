package com.example.a_uction.model.auction.dto;

import com.example.a_uction.model.auction.constants.AuctionStatus;
import com.example.a_uction.model.auction.constants.Category;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.constants.TransactionStatus;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import lombok.*;

import java.time.LocalDateTime;


public class AuctionDto {

    @Getter
    @Setter
    @Builder //테스트
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String itemName;
        private ItemStatus itemStatus;
        private int startingPrice;
        private int minimumBid;
//        private Category category;
        private AuctionStatus auctionStatus;
        private LocalDateTime startDateTime;
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
