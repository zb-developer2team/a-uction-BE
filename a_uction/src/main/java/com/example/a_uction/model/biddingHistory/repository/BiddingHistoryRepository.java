package com.example.a_uction.model.biddingHistory.repository;

import com.example.a_uction.model.biddingHistory.entity.BiddingHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BiddingHistoryRepository extends JpaRepository<BiddingHistoryEntity, Long> {
    Optional<BiddingHistoryEntity> findFirstByAuctionIdOrderByCreatedDateDesc(Long auctionId);
}