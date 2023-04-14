package com.example.a_uction.controller;

import com.example.a_uction.model.biddingHistory.dto.BiddingHistoryDto;
import com.example.a_uction.service.biddingHistory.BiddingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/biddingHistory")
public class BiddingHistoryController {
    private final BiddingHistoryService biddingHistoryService;

    @PostMapping
    public ResponseEntity<?> createBiddingHistory(Principal principal, @RequestBody @Valid BiddingHistoryDto.Request request){
        return ResponseEntity.ok(biddingHistoryService.createBiddingHistory(principal.getName(), request));
    }
}
