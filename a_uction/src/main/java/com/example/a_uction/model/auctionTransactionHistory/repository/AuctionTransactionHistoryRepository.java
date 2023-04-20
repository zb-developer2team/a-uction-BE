package com.example.a_uction.model.auctionTransactionHistory.repository;

import com.example.a_uction.model.auctionTransactionHistory.entity.AuctionTransactionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionTransactionHistoryRepository extends JpaRepository<AuctionTransactionHistoryEntity, Long> {
}
