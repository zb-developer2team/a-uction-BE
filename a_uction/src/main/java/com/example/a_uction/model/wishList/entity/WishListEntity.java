package com.example.a_uction.model.wishList.entity;

import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.user.entity.UserEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class WishListEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity wishUser;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private AuctionEntity wishAuction;
}
