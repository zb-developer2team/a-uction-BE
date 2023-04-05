package com.example.a_uction.service;

import com.example.a_uction.model.auction.constants.AuctionStatus;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    @Mock
    private AuctionRepository auctionRepository;

    @InjectMocks
    private AuctionService auctionService;

    @Test
    @DisplayName("경매 등록 성공")
    void addAuction_SUCCESS() {
        //given
        given(auctionRepository.save(any()))
                .willReturn(AuctionEntity.builder()
                        .itemName("test item")
                        .itemStatus(ItemStatus.BAD)
                        .startingPrice(2000)
                        .minimumBid(200)
                        .startDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
                        .endDateTime(LocalDateTime.parse("2023-04-15T17:10:42.411"))
                        .auctionStatus(AuctionStatus.SCHEDULED)
                        .build());
        //when
        AuctionDto auctionDto = auctionService.addAuction(
                AuctionEntity.builder()
                .itemName("test item2")
                .itemStatus(ItemStatus.GOOD)
                .startingPrice(1000)
                .minimumBid(100)
                .startDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
                .endDateTime(LocalDateTime.parse("2023-04-15T17:10:42.411"))
                .build());
        //then
        ArgumentCaptor<AuctionEntity> captor = ArgumentCaptor.forClass(AuctionEntity.class);
        verify(auctionRepository, times(1)).save(captor.capture());
        assertEquals("test item2", captor.getValue().getItemName());
        assertEquals("test item", auctionDto.getItemName());
        assertEquals(ItemStatus.GOOD, captor.getValue().getItemStatus());
        assertEquals(ItemStatus.BAD, auctionDto.getItemStatus());
        assertEquals(1000, captor.getValue().getStartingPrice());
        assertEquals(2000, auctionDto.getStartingPrice());
        assertEquals(100, captor.getValue().getMinimumBid());
        assertEquals(200, auctionDto.getMinimumBid());
        assertEquals(AuctionStatus.SCHEDULED, auctionDto.getAuctionStatus());
        assertEquals(LocalDateTime.parse("2023-04-15T17:09:42.411"), auctionDto.getStartDateTime());
        assertEquals(LocalDateTime.parse("2023-04-15T17:10:42.411"), auctionDto.getEndDateTime());
    }

    @Test
    @DisplayName("경매 등록 실패- 경매 시작/종료 날짜 확인")
    void addAuction_FAIL_DATE() {
        //given
        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> auctionService.addAuction(
                        AuctionEntity.builder()
                        .itemName("test item2")
                        .itemStatus(ItemStatus.GOOD)
                        .startingPrice(1000)
                        .minimumBid(100)
                        .startDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
                        .endDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
                        .build()));
        //then
        assertEquals(new RuntimeException().getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("경매 등록 실패- 경매 시작 날짜 확인")
    void addAuction_FAIL_STARTDATE() {
        //given
        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> auctionService.addAuction(
                        AuctionEntity.builder()
                                .itemName("test item2")
                                .itemStatus(ItemStatus.GOOD)
                                .startingPrice(1000)
                                .minimumBid(100)
                                .startDateTime(LocalDateTime.parse("2022-04-15T17:09:42.411"))
                                .endDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
                                .build()));
        //then
        assertEquals(new RuntimeException().getMessage(), exception.getMessage());
    }
}