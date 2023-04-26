package com.example.a_uction.model.auctionSearch.repository;

import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auctionSearch.entity.AuctionDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionSearchRepository extends ElasticsearchRepository<AuctionDocument,Long> {
    Page<AuctionDocument> findByStartingPriceOrderByIdAsc(int startingPrice, Pageable pageable);
    Page<AuctionDocument> findByMinimumBidOrderByIdAsc(int minimumBid, Pageable pageable);
    Page<AuctionDocument> findByItemStatusOrderByIdAsc(ItemStatus itemStatus, Pageable pageable);
}
