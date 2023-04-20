package com.example.a_uction.model.biddingHistory.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class BiddingHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long biddingHistoryId;

    private Long auctionId;

    private int price;

    private Long bidderId;

    private boolean bidding_result;

    @CreatedDate
    private LocalDateTime createdDate;

}
