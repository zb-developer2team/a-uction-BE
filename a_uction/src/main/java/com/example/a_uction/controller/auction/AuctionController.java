package com.example.a_uction.controller.auction;


import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.service.auction.AuctionSearchService;
import com.example.a_uction.service.auction.AuctionService;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
@Slf4j
public class AuctionController {

    private final AuctionService auctionService;
    private final AuctionSearchService auctionSearchService;

    @PostMapping
    public ResponseEntity<AuctionDto.Response> addAuction(@RequestBody @Valid AuctionDto.Request auction,
                                                          BindingResult bindingResult,
                                                          Principal principal) {
        if (bindingResult.hasErrors()){
            throw new AuctionException(ErrorCode.INVALID_REQUEST);
        }
        AuctionDto.Response response = auctionService.addAuction(auction, principal.getName());
        auctionSearchService.saveAuctionDocuments(response.getAuctionId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{auctionId}")
    public ResponseEntity<AuctionDto.Response> deleteAuctionByAuctionId(@PathVariable Long auctionId, Principal principal){
        AuctionDto.Response response = auctionService.deleteAuction(auctionId, principal.getName());
        auctionSearchService.deleteAuctionDocuments(auctionId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<AuctionDto.Response> updateAction(@RequestBody AuctionDto.Request updateAuction,
                                          @RequestParam Long auctionId,Principal principal){
        AuctionDto.Response response = auctionService.updateAuction(updateAuction, principal.getName(), auctionId);
        auctionSearchService.saveAuctionDocuments(auctionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-auctions")
    public ResponseEntity<Page<AuctionDto.Response>> getAllAuctionListByUserId(Principal principal, Pageable pageable){
        return ResponseEntity.ok(auctionService.getAllAuctionListByUserEmail(principal.getName(), pageable));
    }

    @GetMapping("/detail/{auctionId}")
    public ResponseEntity<AuctionDto.Response> getAuctionByAuctionId(@PathVariable Long auctionId){
        return ResponseEntity.ok(auctionService.getAuctionByAuctionId(auctionId));
    }

    @GetMapping("/listAll")
    public ResponseEntity<?> getAllAuctions() {
        return null;
    }


}
