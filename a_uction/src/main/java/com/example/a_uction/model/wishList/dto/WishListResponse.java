package com.example.a_uction.model.wishList.dto;

import com.example.a_uction.model.wishList.entity.WishListEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishListResponse {
    private Long id;
    private String userEmail;
    private String itemName;

    public WishListResponse fromEntity(WishListEntity wishListEntity){
        return WishListResponse.builder()
                .id(wishListEntity.getId())
                .userEmail(wishListEntity.getWishUser().getUserEmail())
                .itemName(wishListEntity.getWishAuction().getItemName())
                .build();
    }
}
