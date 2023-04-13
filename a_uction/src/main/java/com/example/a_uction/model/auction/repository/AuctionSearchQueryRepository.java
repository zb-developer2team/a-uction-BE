package com.example.a_uction.model.auction.repository;


import com.example.a_uction.model.auction.dto.SearchCondition;
import com.example.a_uction.model.auction.entity.AuctionDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class AuctionSearchQueryRepository {

    private final ElasticsearchOperations operations;

    public List<AuctionDocument> findByCondition(SearchCondition searchCondition, Pageable pageable) {
        CriteriaQuery query = createConditionCriteriaQuery(searchCondition).setPageable(pageable);

        SearchHits<AuctionDocument> search = operations.search(query, AuctionDocument.class);
        return search.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    private CriteriaQuery createConditionCriteriaQuery(SearchCondition searchCondition) {
        CriteriaQuery query = new CriteriaQuery(new Criteria());

        if (searchCondition == null)
            return query;

        if (searchCondition.getAuctionId() != null)
            query.addCriteria(Criteria.where("auctionId").is(searchCondition.getAuctionId()));

        if(searchCondition.getStartingPrice() > 0)
            query.addCriteria(Criteria.where("startingPrice").is(searchCondition.getStartingPrice()));

        if(searchCondition.getMinimumBid() > 0)
            query.addCriteria(Criteria.where("minimumBid").is(searchCondition.getMinimumBid()));

        if(StringUtils.hasText(searchCondition.getItemName()))
            query.addCriteria(Criteria.where("itemName").is(searchCondition.getItemName()));

        if(StringUtils.hasText(searchCondition.getUserEmail()))
            query.addCriteria(Criteria.where("userEmail").is(searchCondition.getUserEmail()));

        if(searchCondition.getItemStatus() != null)
            query.addCriteria(Criteria.where("itemStatus").is(searchCondition.getItemStatus()));

        if(searchCondition.getTransactionStatus() != null)
            query.addCriteria(Criteria.where("transactionStatus").is(searchCondition.getTransactionStatus()));

        if(searchCondition.getCategory() != null)
            query.addCriteria(Criteria.where("category").is(searchCondition.getCategory()));

        return query;
    }

    public List<AuctionDocument> findByStartWithItemName(String itemName, Pageable pageable) {
        Criteria criteria = Criteria.where("itemName").startsWith(itemName);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        SearchHits<AuctionDocument> search = operations.search(query, AuctionDocument.class);
        return search.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    // description 이 매칭되는 것들에 맞게 score 계산해서 찾아준다.
    public List<AuctionDocument> findByMatchesDescription(String description, Pageable pageable) {
        Criteria criteria = Criteria.where("description").matches(description);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        SearchHits<AuctionDocument> search = operations.search(query, AuctionDocument.class);
        return search.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public List<AuctionDocument> findByContainsDescription(String description, Pageable pageable) {
        Criteria criteria = Criteria.where("description").contains(description);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        SearchHits<AuctionDocument> search = operations.search(query, AuctionDocument.class);
        return search.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

}
