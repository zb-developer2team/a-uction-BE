package com.example.a_uction.controller.search;


import com.example.a_uction.model.auction.constants.Category;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auctionSearch.dto.AuctionDocumentResponse;
import com.example.a_uction.model.auctionSearch.dto.SortingCondition;
import com.example.a_uction.model.auctionSearch.dto.SearchCondition;
import com.example.a_uction.service.search.AuctionSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
public class AuctionSearchController {

    private final AuctionSearchService auctionSearchService;

    //es 등록
    @PostMapping("/auctionDocument/{auctionId}")
    public ResponseEntity<Void> saveAuctionDocuments(@PathVariable Long auctionId){
        auctionSearchService.saveAuctionDocument(auctionId);
        return ResponseEntity.ok().build();
    }

    //시작금액
    @GetMapping("/auctions/startingPrice")
    public ResponseEntity<Page<AuctionDocumentResponse>> searchByNickname(@RequestParam int startingPrice, Pageable pageable){
        return ResponseEntity.ok(auctionSearchService.findByStartingPrice(startingPrice,pageable));
    }

    //minimumBid
    @GetMapping("/auctions/minimumBid")
    public ResponseEntity<Page<AuctionDocumentResponse>> searchByMinimumBid(@RequestParam int minimumBid, Pageable pageable){
        return ResponseEntity.ok(auctionSearchService.findByMinimumBid(minimumBid,pageable));
    }

    //itemStatus
    @GetMapping("/auctions/itemStatus")
    public ResponseEntity<Page<AuctionDocumentResponse>> searchByItemStatus(@RequestParam ItemStatus itemStatus, Pageable pageable){
        return ResponseEntity.ok(auctionSearchService.findByItemStatus(itemStatus,pageable));
    }

    //category
    @GetMapping("/auctions/category")
    public ResponseEntity<Page<AuctionDocumentResponse>> searchByCategory(@RequestParam Category category,
                                                                          SortingCondition condition, Pageable pageable){
        return ResponseEntity.ok(auctionSearchService.findByCategory(category,condition, pageable));
    }

    //검색어 상태별 //종합검색
    @GetMapping("/auctions")
    public ResponseEntity<Page<AuctionDocumentResponse>> searchByName(SearchCondition searchCondition, Pageable pageable){
        return ResponseEntity.ok(auctionSearchService.searchByCondition(searchCondition,pageable));
    }

    //item
    @GetMapping("/auctions/itemName/contain")
    public ResponseEntity<Page<AuctionDocumentResponse>> searchByContainItemName(@RequestParam String itemName,
                                                                                   SortingCondition condition, Pageable pageable){
        return ResponseEntity.ok(auctionSearchService.findByContainItemName(itemName, condition, pageable));
    }

    //description match
    @GetMapping("/auctions/description/matches")
    public ResponseEntity<Page<AuctionDocumentResponse>> searchByMatchesDescription(@RequestParam String description, Pageable pageable){
        return ResponseEntity.ok(auctionSearchService.findByMatchesDescription(description,pageable));
    }

    //description contains
    @GetMapping("/auctions/description/contains")
    public ResponseEntity<Page<AuctionDocumentResponse>> searchByContainsDescription(@RequestParam String description, Pageable pageable){
        return ResponseEntity.ok(auctionSearchService.findByContainsDescription(description,pageable));
    }

    //delete
    @DeleteMapping("/auctionDocument/{auctionId}")
    public ResponseEntity<Void> deleteAuctionDocuments(@PathVariable Long auctionId){
        auctionSearchService.deleteAuctionDocuments(auctionId);
        return ResponseEntity.ok().build();
    }
}
