package com.example.a_uction.service.auction;


import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.auction.constants.Category;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.dto.AuctionDocumentResponse;
import com.example.a_uction.model.auction.dto.SearchCondition;
import com.example.a_uction.model.auction.entity.AuctionDocument;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import com.example.a_uction.model.auction.repository.AuctionSearchQueryRepository;
import com.example.a_uction.model.auction.repository.AuctionSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.a_uction.exception.constants.ErrorCode.AUCTION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuctionSearchService {

    private final AuctionRepository auctionRepository;
    private final AuctionSearchRepository auctionSearchRepository;
    private final AuctionSearchQueryRepository auctionSearchQueryRepository;

    @Transactional
    public void saveAuctionDocuments(Long auctionId) {
        AuctionDocument result = auctionRepository.findById(auctionId).map(AuctionDocument::from)
                .orElseThrow(() -> new AuctionException(AUCTION_NOT_FOUND));
        auctionSearchRepository.save(result);
    }

    @Transactional
    public void deleteAuctionDocuments(Long auctionId) {
        auctionSearchRepository.deleteById(auctionId);
    }

    public List<AuctionDocumentResponse> findByStartingPrice(int startingPrice, Pageable pageable) {
        return auctionSearchRepository.findByStartingPrice(startingPrice, pageable)
                .stream()
                .map(AuctionDocumentResponse::from)
                .collect(Collectors.toList());
    }

    public List<AuctionDocumentResponse> findByMinimumBid(int minimumBid, Pageable pageable) {
        return auctionSearchRepository.findByMinimumBid(minimumBid, pageable)
                .stream()
                .map(AuctionDocumentResponse::from)
                .collect(Collectors.toList());
    }

    public List<AuctionDocumentResponse> findByItemStatus(ItemStatus itemStatus, Pageable pageable) {
        return auctionSearchRepository.findByItemStatus(itemStatus, pageable)
                .stream()
                .map(AuctionDocumentResponse::from)
                .collect(Collectors.toList());
    }

    public List<AuctionDocumentResponse> findByCategory(Category category, Pageable pageable) {
        return auctionSearchRepository.findByCategory(category, pageable)
                .stream()
                .map(AuctionDocumentResponse::from)
                .collect(Collectors.toList());
    }

    public List<AuctionDocumentResponse> searchByCondition(SearchCondition searchCondition, Pageable pageable) {
        return auctionSearchQueryRepository.findByCondition(searchCondition, pageable)
                .stream()
                .map(AuctionDocumentResponse::from)
                .collect(Collectors.toList());
    }

    public List<AuctionDocumentResponse> findByStartWithItemName(String itemName, Pageable pageable) {
        return auctionSearchQueryRepository.findByStartWithItemName(itemName, pageable)
                .stream()
                .map(AuctionDocumentResponse::from)
                .collect(Collectors.toList());
    }

    public List<AuctionDocumentResponse> findByMatchesDescription(String description, Pageable pageable) {
        return auctionSearchQueryRepository.findByMatchesDescription(description, pageable)
                .stream()
                .map(AuctionDocumentResponse::from)
                .collect(Collectors.toList());
    }

    public List<AuctionDocumentResponse> findByContainsDescription(String description, Pageable pageable) {
        return auctionSearchQueryRepository.findByContainsDescription(description, pageable)
                .stream()
                .map(AuctionDocumentResponse::from)
                .collect(Collectors.toList());
    }

}
