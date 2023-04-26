package com.example.a_uction.model.auctionSearch.entity;

import com.example.a_uction.model.auction.constants.Category;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.constants.TransactionStatus;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import javax.persistence.Id;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@Document(indexName = "auction")
@Mapping(mappingPath = "elastic/auction-mapping.json")
@Setting(settingPath = "elastic/auction-setting.json")
public class AuctionDocument {
    @Id
    private Long id;
    private String userEmail;

    private String itemName;

    private ItemStatus itemStatus;
    private TransactionStatus transactionStatus;

    private int startingPrice;
    private int minimumBid;
    private Category category;

    private String description;

    public static AuctionDocument from(AuctionEntity auctionEntity){
        return AuctionDocument.builder()
                .id(auctionEntity.getAuctionId())
                .itemName(auctionEntity.getItemName())
                .userEmail(auctionEntity.getUser().getUserEmail())
                .itemStatus(auctionEntity.getItemStatus())
                .transactionStatus(auctionEntity.getTransactionStatus())
                .startingPrice(auctionEntity.getStartingPrice())
                .minimumBid(auctionEntity.getMinimumBid())
                .category(auctionEntity.getCategory())
                .description(auctionEntity.getDescription())
                .build();
    }

}
