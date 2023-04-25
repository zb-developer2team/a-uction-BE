package com.example.a_uction.controller.auction;


import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.auction.constants.AuctionStatus;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.service.search.AuctionSearchService;
import com.example.a_uction.service.auction.AuctionService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
@Slf4j
public class AuctionController {

    private final AuctionService auctionService;
    private final AuctionSearchService auctionSearchService;

    @PostMapping
    public ResponseEntity<AuctionDto.Response> addAuction(
        @Valid @RequestPart("auction") AuctionDto.Request auction,
        @RequestPart(value = "files", required = false) List<MultipartFile> files,
        BindingResult bindingResult,
        Principal principal) {
        if (bindingResult.hasErrors()) {
            throw new AuctionException(ErrorCode.INVALID_REQUEST);
        }
        AuctionDto.Response response = auctionService.addAuction(auction, files,
            principal.getName());
        auctionSearchService.saveAuctionDocument(response.getAuctionId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/add-image")
    public ResponseEntity<AuctionDto.Response> addImage(@RequestParam Long auctionId,
        @RequestPart MultipartFile file, Principal principal) {

        return ResponseEntity.ok(
            auctionService.addAuctionImage(file, principal.getName(), auctionId)
        );
    }

    @PutMapping("/update/delete-image")
    public ResponseEntity<AuctionDto.Response> deleteImage(@RequestParam Long auctionId,
        @RequestBody Map<String, String> fileUrl, Principal principal) {

        return ResponseEntity.ok(
            auctionService.deleteAuctionImage(fileUrl.get("fileUrl"), principal.getName(), auctionId)
        );
    }


    @DeleteMapping("/{auctionId}")
    public ResponseEntity<AuctionDto.Response> deleteAuctionByAuctionId(
        @PathVariable Long auctionId, Principal principal) {
        AuctionDto.Response response = auctionService.deleteAuction(auctionId, principal.getName());
        auctionSearchService.deleteAuctionDocuments(auctionId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<AuctionDto.Response> updateAction(
        @Valid @RequestPart("auction") AuctionDto.Request updateAuction,
        @RequestPart(value = "files", required = false) List<MultipartFile> files,
        @RequestParam Long auctionId, Principal principal) {
        AuctionDto.Response response = auctionService.updateAuction(updateAuction, files,
            principal.getName(), auctionId);
        auctionSearchService.saveAuctionDocument(auctionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-auctions")
    public ResponseEntity<Page<AuctionDto.Response>> getAllAuctionListByUserId(Principal principal,
        Pageable pageable) {
        return ResponseEntity.ok(
            auctionService.getAllAuctionListByUserEmail(principal.getName(), pageable));
    }

    @GetMapping("/detail/{auctionId}")
    public ResponseEntity<AuctionDto.Response> getAuctionByAuctionId(@PathVariable Long auctionId) {
        return ResponseEntity.ok(auctionService.getAuctionByAuctionId(auctionId));
    }

    @GetMapping("/listAll")
    public ResponseEntity<Page<AuctionDto.Response>> getAllAuctionList(
        @RequestParam(required = false, value = "status") AuctionStatus status, Pageable pageable) {
        return ResponseEntity.ok(auctionService.getAllAuctionListByStatus(status, pageable));
    }

    @GetMapping("/{auctionId}/end")
    public ResponseEntity<AuctionDto.Response> auctionFinished(@PathVariable Long auctionId){
        return ResponseEntity.ok(auctionService.auctionFinished(auctionId));
    }


}
