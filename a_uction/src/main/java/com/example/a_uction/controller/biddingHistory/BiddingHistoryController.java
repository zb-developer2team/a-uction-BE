package com.example.a_uction.controller.biddingHistory;

import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.model.auctionTransactionHistory.dto.AuctionTransactionHistoryDto;
import com.example.a_uction.model.biddingHistory.dto.BiddingHistoryDto;
import com.example.a_uction.service.biddingHistory.BiddingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BiddingHistoryController {

    private final BiddingHistoryService biddingHistoryService;

    @GetMapping("/mypage/joined_auctions")
    public ResponseEntity<List<AuctionDto.Response>> getAllJoinedAuctions(Principal principal){
        return ResponseEntity.ok(biddingHistoryService.getAllAuctions(principal.getName()));
    }

    @GetMapping("/biddingHistories/{auctionId}")
    public ResponseEntity<List<BiddingHistoryDto.Response>> getAllBiddingHistoryByAuctionId(@PathVariable Long auctionId){
        return ResponseEntity.ok(biddingHistoryService.getAllBiddingHistoryByAuctionId(auctionId));
    }

    @GetMapping("/auctions/getCurrentPrice/{auctionId}")
    public ResponseEntity<Integer> getCurrentPrice(@PathVariable Long auctionId){
        return ResponseEntity.ok(biddingHistoryService.getCurrentPrice(auctionId));
    }
}
