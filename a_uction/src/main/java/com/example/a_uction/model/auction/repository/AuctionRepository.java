package com.example.a_uction.model.auction.repository;

import com.example.a_uction.model.auction.constants.AuctionStatus;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<AuctionEntity, Long> {
    Optional<AuctionEntity> findByUserIdAndAuctionId(String userId, Long auctionId);

    Page<AuctionEntity> findByUserId(String userId, Pageable pageable);

    Page<AuctionEntity> findByUserIdAndAuctionStatus(String userId, AuctionStatus auctionStatus, Pageable pageable);

}
