package com.example.a_uction.controller.wish;

import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.model.wishList.dto.WishListResponse;
import com.example.a_uction.service.wish.WishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/wish")
@RequiredArgsConstructor
@Slf4j
public class WishController {

    private final WishService wishService;

    @PostMapping("/{auctionId}")
    public ResponseEntity<WishListResponse> addWish(@PathVariable Long auctionId, Principal principal){
        return ResponseEntity.ok(wishService.addWishAuction(auctionId, principal.getName()));
    }

    @GetMapping("/my-list")
    public ResponseEntity<Page<AuctionDto.Response>> getUserWishList(Principal principal, Pageable pageable){
        return ResponseEntity.ok(wishService.getUserWishList(principal.getName(), pageable));
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<Page<String>> getWishUserListAboutAuction(@PathVariable Long auctionId,
                                                                    Principal principal, Pageable pageable){
        return ResponseEntity.ok(
                wishService.getWishUserListAboutAuction(auctionId, principal.getName(), pageable));
    }

    @DeleteMapping("/{auctionId}")
    public ResponseEntity<WishListResponse> deleteWish(@PathVariable Long auctionId, Principal principal){
        return ResponseEntity.ok(wishService.deleteWishAuction(auctionId, principal.getName()));
    }
}
