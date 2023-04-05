package com.example.a_uction.model.auction.dto;

import com.example.a_uction.model.auction.constants.AuctionStatus;
import com.example.a_uction.model.auction.constants.Category;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.constants.TransactionStatus;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder //test 용
@NoArgsConstructor
@AllArgsConstructor
public class AuctionDto {

    private String itemName;
    private ItemStatus itemStatus;
    private TransactionStatus transactionStatus;
    private AuctionStatus auctionStatus;
    private int startingPrice;
    private int minimumBid;
    private Category category;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public AuctionDto toDto(AuctionEntity auctionEntity){
        AuctionDto auctionDto = new AuctionDto();
        auctionDto.setItemName(auctionEntity.getItemName());
        auctionDto.setItemStatus(auctionEntity.getItemStatus());
        auctionDto.setAuctionStatus(auctionEntity.getAuctionStatus());
        auctionDto.setTransactionStatus(auctionEntity.getTransactionStatus());
        auctionDto.setStartingPrice(auctionEntity.getStartingPrice());
        auctionDto.setMinimumBid(auctionEntity.getMinimumBid());
        auctionDto.setCategory(auctionEntity.getCategory());
        auctionDto.setStartDateTime(auctionEntity.getStartDateTime());
        auctionDto.setEndDateTime(auctionEntity.getEndDateTime());
        return auctionDto;
    }
}
