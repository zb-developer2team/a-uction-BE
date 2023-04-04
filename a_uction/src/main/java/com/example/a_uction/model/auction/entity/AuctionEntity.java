package com.example.a_uction.model.auction.entity;

import com.example.a_uction.model.auction.constants.AuctionStatus;
import com.example.a_uction.model.auction.constants.Category;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.constants.TransactionStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AuctionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int auctionId;
    //@ManyToOne(targetEntity = .class, fetch = FetchType.LAZY)
    private int userId;
    private String itemName;

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;
    @Enumerated(EnumType.STRING)
    private AuctionStatus auctionStatus;

    private int buyerId;
    private int startingPrice;
    private int minimumBid;
    @Enumerated(EnumType.STRING)
    private Category category;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;

}
