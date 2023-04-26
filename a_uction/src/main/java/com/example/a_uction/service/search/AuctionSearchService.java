package com.example.a_uction.service.search;


import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.auction.constants.Category;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auctionSearch.dto.AuctionDocumentResponse;
import com.example.a_uction.model.auctionSearch.dto.ItemNameSearchCondition;
import com.example.a_uction.model.auctionSearch.dto.SearchCondition;
import com.example.a_uction.model.auctionSearch.entity.AuctionDocument;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import com.example.a_uction.model.auctionSearch.repository.AuctionSearchQueryRepository;
import com.example.a_uction.model.auctionSearch.repository.AuctionSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.a_uction.exception.constants.ErrorCode.AUCTION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuctionSearchService {

    private final AuctionRepository auctionRepository;
    private final AuctionSearchRepository auctionSearchRepository;
    private final AuctionSearchQueryRepository auctionSearchQueryRepository;

    @Transactional
    public void saveAuctionDocument(Long auctionId) {
        AuctionDocument result = auctionRepository.findById(auctionId).map(AuctionDocument::from)
                .orElseThrow(() -> new AuctionException(AUCTION_NOT_FOUND));
        auctionSearchRepository.save(result);
    }

    @Transactional
    public void deleteAuctionDocuments(Long auctionId) {
        auctionSearchRepository.deleteById(auctionId);
    }

    public Page<AuctionDocumentResponse> findByStartingPrice(int startingPrice, Pageable pageable) {
        return auctionSearchRepository.findByStartingPriceOrderByIdAsc(startingPrice, pageable)
                .map(AuctionDocumentResponse::from);

    }

    public Page<AuctionDocumentResponse> findByMinimumBid(int minimumBid, Pageable pageable) {
        return auctionSearchRepository.findByMinimumBidOrderByIdAsc(minimumBid, pageable)
                .map(AuctionDocumentResponse::from);
    }

    public Page<AuctionDocumentResponse> findByItemStatus(ItemStatus itemStatus, Pageable pageable) {
        return auctionSearchRepository.findByItemStatusOrderByIdAsc(itemStatus, pageable)
                .map(AuctionDocumentResponse::from);
    }

    public Page<AuctionDocumentResponse> findByCategory(Category category, Pageable pageable) {
        return auctionSearchRepository.findByCategoryOrderByIdAsc(category, pageable)
                .map(AuctionDocumentResponse::from);
    }

    public Page<AuctionDocumentResponse> searchByCondition(SearchCondition searchCondition, Pageable pageable) {
        return auctionSearchQueryRepository.findByCondition(searchCondition, pageable)
                .map(AuctionDocumentResponse::from);
    }

    public Page<AuctionDocumentResponse> findByStartWithItemName(ItemNameSearchCondition condition, Pageable pageable) {
        return auctionSearchQueryRepository.findByStartWithItemName(condition, pageable)
                .map(AuctionDocumentResponse::from);
    }

    public Page<AuctionDocumentResponse> findByMatchesDescription(String description, Pageable pageable) {
        return auctionSearchQueryRepository.findByMatchesDescription(description, pageable)
                .map(AuctionDocumentResponse::from);
    }

    public Page<AuctionDocumentResponse> findByContainsDescription(String description, Pageable pageable) {
        return auctionSearchQueryRepository.findByContainsDescription(description, pageable)
                .map(AuctionDocumentResponse::from);
    }

}
