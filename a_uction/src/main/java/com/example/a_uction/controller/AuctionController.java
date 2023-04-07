package com.example.a_uction.controller;


import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auction")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    public ResponseEntity<AuctionDto.Response> addAuction(@RequestBody @Valid AuctionDto.Request auction, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            throw new RuntimeException("잘못된 인풋입니다.");
        }
        return ResponseEntity.ok(auctionService.addAuction(auction));
    }

    @PutMapping
    public ResponseEntity<?> updateAction(@RequestBody AuctionDto.Request updateAuction,
                                          @RequestParam Long auctionId){
        //TODO userId 토큰을 통해 받을 예정
        int userId = 0;
        return ResponseEntity.ok(auctionService.updateAuction(updateAuction, userId, auctionId));
    }
}
