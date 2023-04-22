package com.example.a_uction.model.auction.dto;

import com.example.a_uction.model.auction.constants.Category;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.constants.TransactionStatus;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.user.entity.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;


public class AuctionDto {

    @Getter
    @Setter
    @Builder //테스트
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Request {
        @NotNull(message = "상품 이름을 입력하세요")
        private String itemName;
        @NotNull(message = "상품의 상태를 입력하세요")
        private ItemStatus itemStatus;
        @Min(value = 100, message = "최소 시작 금액은 100원 이상입니다.")
        private int startingPrice;
        @Min(value = 100, message = "최소 호가 단위는 100원 이상입니다.")
        private int minimumBid;
        @Enumerated(EnumType.STRING)
        private Category category;
        @NotNull(message = "경매 시작 시간을 입력하세요")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime startDateTime;
        @NotNull(message = "경매 종료 시간을 입력하세요")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime endDateTime;
        @NotNull(message = "상품 설명을 입력하세요")
        private String description;

        // 테스트 위해 주석처리
        // @NotNull(message = "상품 이미지를 등록해주세요")
        private List<MultipartFile> files;

        public AuctionEntity toEntity(UserEntity user){
            return AuctionEntity.builder()
                    .user(user)
                    .itemName(this.itemName)
                    .itemStatus(this.itemStatus)
                    .startingPrice(this.startingPrice)
                    .minimumBid(this.minimumBid)
                    .startDateTime(this.startDateTime)
                    .endDateTime(this.endDateTime)
                    .category(this.category)
                    .description((this.description))
                    .build();
        }

    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private Long auctionId;
        private String itemName;
        private ItemStatus itemStatus;
        private TransactionStatus transactionStatus;
        private int startingPrice;
        private int minimumBid;
        private Category category;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private String description;
        private List<String> files;
        private Long buyerId;


        public AuctionDto.Response fromEntity(AuctionEntity auctionEntity){
            // 파일 없이 테스트 위해 작성
            if (auctionEntity.getFiles() == null) {
                auctionEntity.setFiles(new ArrayList<>());
            }

            return Response.builder()
                    .auctionId(auctionEntity.getAuctionId())
                    .itemName(auctionEntity.getItemName())
                    .itemStatus(auctionEntity.getItemStatus())
                    .transactionStatus(auctionEntity.getTransactionStatus())
                    .startingPrice(auctionEntity.getStartingPrice())
                    .minimumBid(auctionEntity.getMinimumBid())
                    .category(auctionEntity.getCategory())
                    .startDateTime(auctionEntity.getStartDateTime())
                    .endDateTime(auctionEntity.getEndDateTime())
                    .description(auctionEntity.getDescription())
                    .files(auctionEntity.getFiles().stream()
                        .map(file -> file.getSrc())
                        .collect(Collectors.toList()))
                    .buyerId(auctionEntity.getBuyerId())
                    .build();
        }

    }


}
