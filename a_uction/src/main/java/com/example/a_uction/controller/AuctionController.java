package com.example.a_uction.controller;


import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auction")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    public ResponseEntity<?> addAuction(@RequestBody @Valid AuctionEntity auction, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            throw new RuntimeException("잘못된 인풋입니다.");
        }
        return ResponseEntity.ok(auctionService.addAuction(auction));
    }
}
