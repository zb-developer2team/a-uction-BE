package com.example.a_uction.controller.auction;


import com.example.a_uction.model.auction.constants.Category;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.dto.AuctionDocumentResponse;
import com.example.a_uction.model.auction.dto.SearchCondition;
import com.example.a_uction.service.auction.AuctionSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
public class AuctionSearchController {

    private final AuctionSearchService auctionSearchService;

    //es 등록
    @PostMapping("/auctionDocument/{auctionId}")
    public ResponseEntity<Void> saveAuctionDocuments(@PathVariable Long auctionId){
        auctionSearchService.saveAuctionDocuments(auctionId);
        return ResponseEntity.ok().build();
    }

    //시작금액
    @GetMapping("/auctions/startingPrice")
    public ResponseEntity<List<AuctionDocumentResponse>> searchByNickname(@RequestParam int startingPrice, Pageable pageable){
        return ResponseEntity.ok(auctionSearchService.findByStartingPrice(startingPrice,pageable));
    }

    //minimumBid
    @GetMapping("/auctions/minimumBid")
    public ResponseEntity<List<AuctionDocumentResponse>> searchByMinimumBid(@RequestParam int minimumBid, Pageable pageable){
        return ResponseEntity.ok(auctionSearchService.findByMinimumBid(minimumBid,pageable));
    }

    //itemStatus
    @GetMapping("/auctions/itemStatus")
    public ResponseEntity<List<AuctionDocumentResponse>> searchByMinimumBid(@RequestParam ItemStatus itemStatus, Pageable pageable){
        return ResponseEntity.ok(auctionSearchService.findByItemStatus(itemStatus,pageable));
    }

    //category
    @GetMapping("/auctions/category")
    public ResponseEntity<List<AuctionDocumentResponse>> searchByMinimumBid(@RequestParam Category category, Pageable pageable){
        return ResponseEntity.ok(auctionSearchService.findByCategory(category,pageable));
    }

    //검색어 상태별
    @GetMapping("/auctions")
    public ResponseEntity<List<AuctionDocumentResponse>> searchByName(SearchCondition searchCondition, Pageable pageable){
        return ResponseEntity.ok(auctionSearchService.searchByCondition(searchCondition,pageable));
    }

    //item
    @GetMapping("/auctions/itemName/startWith")
    public ResponseEntity<List<AuctionDocumentResponse>> searchByStartWithItemName(@RequestParam String itemName, Pageable pageable){
        return ResponseEntity.ok(auctionSearchService.findByStartWithItemName(itemName,pageable));
    }

    //description match
    @GetMapping("/auctions/description/matches")
    public ResponseEntity<List<AuctionDocumentResponse>> searchByMatchesDescription(@RequestParam String description, Pageable pageable){
        return ResponseEntity.ok(auctionSearchService.findByMatchesDescription(description,pageable));
    }

    //description contains
    @GetMapping("/auctions/description/contains")
    public ResponseEntity<List<AuctionDocumentResponse>> searchByContainsDescription(@RequestParam String description, Pageable pageable){
        return ResponseEntity.ok(auctionSearchService.findByContainsDescription(description,pageable));
    }
}