package com.example.a_uction.model.auctionSearch.dto;


import com.example.a_uction.model.auction.constants.Category;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.constants.TransactionStatus;
import com.example.a_uction.model.auctionSearch.entity.AuctionDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuctionDocumentResponse {
    private Long auctionId;
    private String itemName;
    private ItemStatus itemStatus;
    private TransactionStatus transactionStatus;
    private int startingPrice;
    private int minimumBid;
    private Category category;
    private String description;

    public static AuctionDocumentResponse from(AuctionDocument auctionDocument){
        return AuctionDocumentResponse.builder()
                .auctionId(auctionDocument.getId())
                .itemName(auctionDocument.getItemName())
                .itemStatus(auctionDocument.getItemStatus())
                .transactionStatus(auctionDocument.getTransactionStatus())
                .startingPrice(auctionDocument.getStartingPrice())
                .minimumBid(auctionDocument.getMinimumBid())
                .category(auctionDocument.getCategory())
                .description(auctionDocument.getDescription())
                .build();
    }
}
