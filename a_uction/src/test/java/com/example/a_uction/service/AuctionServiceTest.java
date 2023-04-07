package com.example.a_uction.service;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.auction.constants.AuctionStatus;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.constants.TransactionStatus;
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
import java.util.Optional;

import static com.example.a_uction.exception.constants.ErrorCode.BEFORE_START_TIME;
import static com.example.a_uction.exception.constants.ErrorCode.END_TIME_EARLIER_THAN_START_TIME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
        AuctionDto.Response response = auctionService.addAuction(
                AuctionDto.Request.builder()
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
        assertEquals("test item", response.getItemName());
        assertEquals(ItemStatus.GOOD, captor.getValue().getItemStatus());
        assertEquals(ItemStatus.BAD, response.getItemStatus());
        assertEquals(1000, captor.getValue().getStartingPrice());
        assertEquals(2000, response.getStartingPrice());
        assertEquals(100, captor.getValue().getMinimumBid());
        assertEquals(200, response.getMinimumBid());
        assertEquals(AuctionStatus.SCHEDULED, response.getAuctionStatus());
        assertEquals(LocalDateTime.parse("2023-04-15T17:09:42.411"), response.getStartDateTime());
        assertEquals(LocalDateTime.parse("2023-04-15T17:10:42.411"), response.getEndDateTime());
    }

    @Test
    @DisplayName("경매 등록 실패- 경매 시작/종료 날짜 확인")
    void addAuction_FAIL_DATE() {
        //given
        //when
        AuctionException exception = assertThrows(AuctionException.class,
                () -> auctionService.addAuction(
                        AuctionDto.Request.builder()
                        .itemName("test item2")
                        .itemStatus(ItemStatus.GOOD)
                        .startingPrice(1000)
                        .minimumBid(100)
                        .startDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
                        .endDateTime(LocalDateTime.parse("2023-04-15T17:09:41.411"))
                        .build()));
        //then
        assertEquals(new AuctionException(END_TIME_EARLIER_THAN_START_TIME).getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("경매 등록 실패- 경매 시작 날짜가 등록일 보다 이름")
    void addAuction_FAIL_STARTDATE() {
        //given
        //when
        RuntimeException exception = assertThrows(AuctionException.class,
                () -> auctionService.addAuction(
                        AuctionDto.Request.builder()
                                .itemName("test item2")
                                .itemStatus(ItemStatus.GOOD)
                                .startingPrice(1000)
                                .minimumBid(100)
                                .startDateTime(LocalDateTime.parse("2022-04-15T17:09:42.411"))
                                .endDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
                                .build()));
        //then
        assertEquals(new AuctionException(BEFORE_START_TIME).getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("경매 아이디로 읽어오기")
    void getAuctionByAuctionIdTest(){
        //given
        given(auctionRepository.findById(anyLong()))
                .willReturn(Optional.of(AuctionEntity.builder()
                        .itemName("test item")
                        .itemStatus(ItemStatus.BAD)
                        .startingPrice(2000)
                        .minimumBid(200)
                        .startDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
                        .endDateTime(LocalDateTime.parse("2023-04-15T17:10:42.411"))
                        .auctionStatus(AuctionStatus.SCHEDULED)
                        .build()));
        //when
        AuctionDto.Response response = auctionService.getAuctionByAuctionId(1L);
        //then
        verify(auctionRepository, times(1)).findById(1L);
        assertEquals("test item", response.getItemName());
        assertEquals(ItemStatus.BAD, response.getItemStatus());
        assertEquals(2000, response.getStartingPrice());
        assertEquals(200, response.getMinimumBid());
        assertEquals(AuctionStatus.SCHEDULED, response.getAuctionStatus());
        assertEquals(LocalDateTime.parse("2023-04-15T17:09:42.411"), response.getStartDateTime());
        assertEquals(LocalDateTime.parse("2023-04-15T17:10:42.411"), response.getEndDateTime());
    }
    @Test
    @DisplayName("경매 아이디로 읽어오기 실패 - 경매 없음")
    void getAuctionByAuctionId_NOTFOUND(){
        //given
        given(auctionRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        //when
        AuctionException exception = assertThrows(AuctionException.class,
                () -> auctionService.getAuctionByAuctionId(1L));
        //then
        verify(auctionRepository, times(1)).findById(1L);
        assertEquals(new AuctionException(ErrorCode.AUCTION_NOT_FOUND).getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("경매 등록 성공")
    void updateAuctionSuccess(){
        //given

        LocalDateTime startTime = LocalDateTime.now().plusDays(3);
        LocalDateTime endTime = LocalDateTime.now().plusDays(10);

        AuctionDto.Request updateAuction = AuctionDto.Request.builder()
                .itemName("item2")
                .startDateTime(startTime)
                .endDateTime(endTime)
                .itemStatus(ItemStatus.GOOD)
                .minimumBid(2000)
                .startingPrice(1000)
                .build();

        AuctionEntity auctionEntity = AuctionEntity.builder()
                .auctionId(1L)
                .userId(1)
                .itemName("item")
                .startingPrice(1234)
                .minimumBid(1000)
                .transactionStatus(TransactionStatus.SALE)
                .itemStatus(ItemStatus.BAD)
                .startDateTime(LocalDateTime.of(2023,4,1,00,00,00))
                .endDateTime(LocalDateTime.of(2023,4,10,00,00,00))
                .build();

        given(auctionRepository.findByUserIdAndAuctionId(anyInt(), anyLong()))
                .willReturn(Optional.ofNullable(auctionEntity));
        given(auctionRepository.save(any())).willReturn(auctionEntity);

        //when
        var result = auctionService.updateAuction(updateAuction, 1, 1L);

        //then
        assertEquals("item2", result.getItemName());
        assertEquals(ItemStatus.GOOD, result.getItemStatus());
        assertEquals(startTime, result.getStartDateTime());
        assertEquals(endTime, result.getEndDateTime());
        assertEquals(2000, result.getMinimumBid());
        assertEquals(1000, result.getStartingPrice());
    }

    @Test
    @DisplayName("경매 등록 실패 - 경매 시작 시간 확인")
    void updateAuctionFailStartTime(){
        //given
        LocalDateTime startTime = LocalDateTime.now().minusDays(3);
        LocalDateTime endTime = LocalDateTime.now().plusDays(10);

        AuctionDto.Request updateAuction = AuctionDto.Request.builder()
                .itemName("item2")
                .startDateTime(startTime)
                .endDateTime(endTime)
                .itemStatus(ItemStatus.GOOD)
                .minimumBid(2000)
                .startingPrice(1000)
                .build();

        AuctionEntity auctionEntity = AuctionEntity.builder()
                .auctionId(1L)
                .userId(1)
                .itemName("item")
                .startingPrice(1234)
                .minimumBid(1000)
                .transactionStatus(TransactionStatus.SALE)
                .itemStatus(ItemStatus.BAD)
                .startDateTime(LocalDateTime.of(2023,4,1,00,00,00))
                .endDateTime(LocalDateTime.of(2023,4,10,00,00,00))
                .build();

        given(auctionRepository.findByUserIdAndAuctionId(anyInt(), anyLong()))
                .willReturn(Optional.ofNullable(auctionEntity));

        //when
        AuctionException exception = assertThrows(AuctionException.class,
                () -> auctionService.updateAuction(updateAuction, 1, 1L));

        //then
        assertEquals(BEFORE_START_TIME,exception.getErrorCode());
    }

    @Test
    @DisplayName("경매 등록 실패 - 경매 종료 시간 확인")
    void updateAuctionFailEndTime(){
        //given
        LocalDateTime startTime = LocalDateTime.now().plusDays(3);
        LocalDateTime endTime = LocalDateTime.now().minusDays(10);

        AuctionDto.Request updateAuction = AuctionDto.Request.builder()
                .itemName("item2")
                .startDateTime(startTime)
                .endDateTime(endTime)
                .itemStatus(ItemStatus.GOOD)
                .minimumBid(2000)
                .startingPrice(1000)
                .build();

        AuctionEntity auctionEntity = AuctionEntity.builder()
                .auctionId(1L)
                .userId(1)
                .itemName("item")
                .startingPrice(1234)
                .minimumBid(1000)
                .transactionStatus(TransactionStatus.SALE)
                .itemStatus(ItemStatus.BAD)
                .startDateTime(LocalDateTime.of(2023,4,1,00,00,00))
                .endDateTime(LocalDateTime.of(2023,4,10,00,00,00))
                .build();

        given(auctionRepository.findByUserIdAndAuctionId(anyInt(), anyLong()))
                .willReturn(Optional.ofNullable(auctionEntity));

        //when
        AuctionException exception = assertThrows(AuctionException.class,
                () -> auctionService.updateAuction(updateAuction, 1, 1L));

        //then
        assertEquals(END_TIME_EARLIER_THAN_START_TIME,exception.getErrorCode());
    }
}