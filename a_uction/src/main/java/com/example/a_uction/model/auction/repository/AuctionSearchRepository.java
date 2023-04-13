package com.example.a_uction.model.auction.repository;

import com.example.a_uction.model.auction.constants.Category;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.entity.AuctionDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionSearchRepository extends ElasticsearchRepository<AuctionDocument,Long> {
    List<AuctionDocument> findByStartingPrice(int startingPrice, Pageable pageable);
    List<AuctionDocument> findByMinimumBid(int minimumBid, Pageable pageable);
    List<AuctionDocument> findByItemStatus(ItemStatus itemStatus, Pageable pageable);
    List<AuctionDocument> findByCategory (Category category, Pageable pageable);
}
