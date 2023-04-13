package com.example.a_uction.model.auction.entity;

import com.example.a_uction.model.auction.constants.Category;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.constants.TransactionStatus;
import com.example.a_uction.model.user.entity.UserEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class AuctionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @NotNull(message = "상품 이름을 입력하세요")
    private String itemName;

    @NotNull(message = "상품의 상태를 입력하세요")
    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    private int buyerId;
    @Min(value = 100, message = "최소 시작 금액은 100원 이상입니다.")
    private int startingPrice;
    @Min(value = 100, message = "최소 호가 단위는 100원 이상입니다.")
    private int minimumBid;
    @Enumerated(EnumType.STRING)
    private Category category;

    @NotNull(message = "경매 시작 시간을 입력하세요")
    private LocalDateTime startDateTime;
    @NotNull(message = "경매 종료 시간을 입력하세요")
    private LocalDateTime endDateTime;

    @CreatedDate
    private LocalDateTime createDateTime;
    @LastModifiedDate
    private LocalDateTime updateDateTime;

    @NotNull(message = "상품 설명을 입력하세요")
    private String description;

}
