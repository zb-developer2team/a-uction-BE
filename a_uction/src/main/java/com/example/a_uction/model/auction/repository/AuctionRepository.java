package com.example.a_uction.model.auction.repository;

import com.example.a_uction.model.auction.entity.AuctionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<AuctionEntity, Long> {
    Optional<AuctionEntity> findByUserIdAndAuctionId(Long userId, Long auctionId);

    Page<AuctionEntity> findByUserId(Long userId, Pageable pageable);

    Page<AuctionEntity> findByStartDateTimeAfter(LocalDateTime startDateTime, Pageable pageable);
    Page<AuctionEntity> findByEndDateTimeBefore(LocalDateTime endDateTime, Pageable pageable);
    Page<AuctionEntity> findByStartDateTimeBeforeAndEndDateTimeAfter(LocalDateTime start, LocalDateTime end, Pageable pageable);

}
