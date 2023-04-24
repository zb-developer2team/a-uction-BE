package com.example.a_uction.model.auctionTransactionHistory.entity;

import java.time.LocalDateTime;
import javax.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AuctionTransactionHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionTransactionHistoryId;

    private String sellerEmail;

    private String itemName;

    private int price;

    private String buyerEmail;

    private String imageSrc;

    @CreatedDate
    private LocalDateTime createDateTime;

}
