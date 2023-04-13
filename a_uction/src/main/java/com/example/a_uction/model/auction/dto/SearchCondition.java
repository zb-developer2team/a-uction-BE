package com.example.a_uction.model.auction.dto;

import com.example.a_uction.model.auction.constants.Category;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.constants.TransactionStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchCondition {

    private Long auctionId;
    private String userEmail;

    private String itemName;

    private ItemStatus itemStatus;
    private TransactionStatus transactionStatus;

    private int startingPrice;
    private int minimumBid;
    private Category category;

}
