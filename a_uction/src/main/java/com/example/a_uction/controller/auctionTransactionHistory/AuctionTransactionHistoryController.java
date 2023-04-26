package com.example.a_uction.controller.auctionTransactionHistory;

import com.example.a_uction.model.auctionTransactionHistory.dto.AuctionTransactionHistoryDto;
import com.example.a_uction.service.auctionTransactionHistory.AuctionTransactionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuctionTransactionHistoryController {

    private final AuctionTransactionHistoryService auctionTransactionHistoryService;

    @GetMapping("/mypage/transaction-history")
    public ResponseEntity<List<AuctionTransactionHistoryDto.Response>> getAllTransactionHistory(Principal principal){
        return ResponseEntity.ok(auctionTransactionHistoryService.getAllTransactionHistory(principal.getName()));
    }
}
