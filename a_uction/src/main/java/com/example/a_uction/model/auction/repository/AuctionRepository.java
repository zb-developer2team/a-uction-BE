package com.example.a_uction.model.auction.repository;

import com.example.a_uction.model.auction.entity.AuctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionRepository extends JpaRepository<AuctionEntity, Long> {
}
