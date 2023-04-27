package com.example.a_uction.model.wishList.repository;

import com.example.a_uction.model.wishList.entity.WishListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishRepository extends JpaRepository<WishListEntity, Long> {
    Optional<WishListEntity> findByWishUserIdAndWishAuctionAuctionId(Long userId, Long auctionId);
}
