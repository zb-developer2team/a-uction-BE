package com.example.a_uction.model.auction.entity;

import com.example.a_uction.model.auction.constants.Category;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.constants.TransactionStatus;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.model.file.entity.FileEntity;
import com.example.a_uction.model.user.entity.UserEntity;
import java.util.ArrayList;
import java.util.List;
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

    private String itemName;
    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;
    private Long buyerId;
    private int startingPrice;
    private int minimumBid;
    @Enumerated(EnumType.STRING)
    private Category category;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @CreatedDate
    private LocalDateTime createDateTime;
    @LastModifiedDate
    private LocalDateTime updateDateTime;
    private String description;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL)
    @Builder.Default
    private List<FileEntity> files = new ArrayList<>();

    public void addFile(FileEntity file) {
        files.add(file);
        file.setAuction(this);
    }
    public void updateEntity(AuctionDto.Request updateAuction) {
        this.setItemName(updateAuction.getItemName());
        this.setItemStatus(updateAuction.getItemStatus());
        this.setStartingPrice(updateAuction.getStartingPrice());
        this.setMinimumBid(updateAuction.getMinimumBid());
        this.setStartDateTime(updateAuction.getStartDateTime());
        this.setEndDateTime(updateAuction.getEndDateTime());
        this.setItemStatus(updateAuction.getItemStatus());
        this.setCategory(updateAuction.getCategory());
        this.setUpdateDateTime(LocalDateTime.now());
        this.setDescription(updateAuction.getDescription());
    }
}
