package com.example.a_uction.model.auctionTransactionHistory.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class AuctionTransactionHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionTransactionHistoryId;

    private String sellerEmail;

    private String itemName;

    private int price;

    private String buyerEmail;

    private String imageSrc;

}
