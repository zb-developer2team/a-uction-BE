package com.example.a_uction.controller;


import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.auction.constants.AuctionStatus;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.service.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/auction")
@RequiredArgsConstructor
@Slf4j
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    public ResponseEntity<AuctionDto.Response> addAuction(@RequestBody @Valid AuctionDto.Request auction,
                                                          BindingResult bindingResult,
                                                          Principal principal) {
        if (bindingResult.hasErrors()){
            throw new AuctionException(ErrorCode.INVALID_REQUEST);
        }
        return ResponseEntity.ok(auctionService.addAuction(auction, principal.getName()));
    }

    @GetMapping("/{auctionId}")
    public ResponseEntity<AuctionDto.Response> getAuctionByAuctionId(@PathVariable Long auctionId){
        return ResponseEntity.ok(auctionService.getAuctionByAuctionId(auctionId));
    }

    @PutMapping
    public ResponseEntity<?> updateAction(@RequestBody AuctionDto.Request updateAuction,
                                          @RequestParam Long auctionId,Principal principal){
        return ResponseEntity.ok(auctionService.updateAuction(updateAuction, principal.getName(), auctionId));
    }


    @GetMapping("/read")
    public ResponseEntity<?> getAllAuctionListByUserId(Principal principal, Pageable pageable){
        return ResponseEntity.ok(auctionService.getAllAuctionListByUserEmail(principal.getName(), pageable));
    }

    @GetMapping("/read/{auctionStatus}")
    public ResponseEntity<?> getAuctionListByUserIdAndStatus(Principal principal, @PathVariable AuctionStatus auctionStatus,
            Pageable pageable){

        return ResponseEntity.ok(auctionService.getAuctionListByUserEmailAndAuctionStatus(
                principal.getName(), auctionStatus, pageable));
    }


}
