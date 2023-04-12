package com.example.a_uction.controller.auction;


import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.service.auction.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/auctions")
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

    @DeleteMapping("/{auctionId}")
    public ResponseEntity<?> deleteAuctionByAuctionId(@PathVariable Long auctionId, Principal principal){
        return ResponseEntity.ok(auctionService.deleteAuction(auctionId, principal.getName()));
    }

    @GetMapping("/{auctionId}")
    public ResponseEntity<AuctionDto.Response> getAuctionByAuctionId(@PathVariable Long auctionId){
        return ResponseEntity.ok(auctionService.getAuctionByAuctionId(auctionId));
    }

    @PutMapping
    public ResponseEntity<AuctionDto.Response> updateAction(@RequestBody AuctionDto.Request updateAuction,
                                          @RequestParam Long auctionId,Principal principal){
        return ResponseEntity.ok(auctionService.updateAuction(updateAuction, principal.getName(), auctionId));
    }


    @GetMapping("/read")
    public ResponseEntity<Page<AuctionDto.Response>> getAllAuctionListByUserId(Principal principal, Pageable pageable){
        return ResponseEntity.ok(auctionService.getAllAuctionListByUserEmail(principal.getName(), pageable));
    }

}