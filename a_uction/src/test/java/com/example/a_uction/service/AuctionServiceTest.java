package com.example.a_uction.service;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.constants.TransactionStatus;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import com.example.a_uction.model.user.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.a_uction.exception.constants.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
                .build(), "user1");
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
                        .build(), "user1"));
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
                                .build(), "user1"));
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
                        .build()));
        //when
        AuctionDto.Response response = auctionService.getAuctionByAuctionId(1L);
        //then
        verify(auctionRepository, times(1)).findById(1L);
        assertEquals("test item", response.getItemName());
        assertEquals(ItemStatus.BAD, response.getItemStatus());
        assertEquals(2000, response.getStartingPrice());
        assertEquals(200, response.getMinimumBid());
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
    @DisplayName("경매 업데이트 성공")
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
                .userEmail("user1")
                .auctionId(1L)
                .itemName("item")
                .startingPrice(1234)
                .minimumBid(1000)
                .transactionStatus(TransactionStatus.SALE)
                .itemStatus(ItemStatus.BAD)
                .startDateTime(LocalDateTime.of(2024,4,1,00,00,00))
                .endDateTime(LocalDateTime.of(2024,4,10,00,00,00))
                .build();

        given(auctionRepository.findByUserEmailAndAuctionId(any(), anyLong()))
                .willReturn(Optional.ofNullable(auctionEntity));
        given(auctionRepository.save(any())).willReturn(auctionEntity);

        //when
        var result = auctionService.updateAuction(updateAuction, "user1", 1L);

        //then
        assertEquals("item2", result.getItemName());
        assertEquals(ItemStatus.GOOD, result.getItemStatus());
        assertEquals(startTime, result.getStartDateTime());
        assertEquals(endTime, result.getEndDateTime());
        assertEquals(2000, result.getMinimumBid());
        assertEquals(1000, result.getStartingPrice());
    }

    @Test
    @DisplayName("경매 업데이트 실패 - 경매 시작 시간 확인")
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
                .userEmail("user1")
                .itemName("item")
                .startingPrice(1234)
                .minimumBid(1000)
                .transactionStatus(TransactionStatus.SALE)
                .itemStatus(ItemStatus.BAD)
                .startDateTime(LocalDateTime.of(2024,4,1,00,00,00))
                .endDateTime(LocalDateTime.of(2025,4,10,00,00,00))
                .build();

        given(auctionRepository.findByUserEmailAndAuctionId(any(), anyLong()))
                .willReturn(Optional.ofNullable(auctionEntity));

        //when
        AuctionException exception = assertThrows(AuctionException.class,
                () -> auctionService.updateAuction(updateAuction, "user1", 1L));

        //then
        assertEquals(BEFORE_START_TIME,exception.getErrorCode());
    }

    @Test
    @DisplayName("경매 업데이트 실패 - 경매 종료 시간 확인")
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
                .userEmail("user1")
                .itemName("item")
                .startingPrice(1234)
                .minimumBid(1000)
                .transactionStatus(TransactionStatus.SALE)
                .itemStatus(ItemStatus.BAD)
                .startDateTime(LocalDateTime.of(2023,5,1,00,00,00))
                .endDateTime(LocalDateTime.of(2023,6,10,00,00,00))
                .build();

        given(auctionRepository.findByUserEmailAndAuctionId(any(), anyLong()))
                .willReturn(Optional.ofNullable(auctionEntity));

        //when
        AuctionException exception = assertThrows(AuctionException.class,
                () -> auctionService.updateAuction(updateAuction, "user1", 1L));

        //then
        assertEquals(END_TIME_EARLIER_THAN_START_TIME,exception.getErrorCode());
    }

    @Test
    @DisplayName("해당 유저의 모든 경매 리스트 가져오기 - 성공")
    void getAllAuctionListByUserIdSuccess(){
        //given

        List<AuctionEntity> list = List.of(
                AuctionEntity.builder()
                        .auctionId(1L)
                        .userEmail("user1")
                        .itemName("item1")
                        .startingPrice(1111)
                        .minimumBid(1000)
                        .transactionStatus(TransactionStatus.SALE)
                        .itemStatus(ItemStatus.BAD)
                        .startDateTime(LocalDateTime.of(2023,4,1,00,00,00))
                        .endDateTime(LocalDateTime.of(2023,4,10,00,00,00))
                        .build(),
                AuctionEntity.builder()
                        .auctionId(2L)
                        .userEmail("user1")
                        .itemName("item2")
                        .startingPrice(2222)
                        .minimumBid(2000)
                        .transactionStatus(TransactionStatus.SALE)
                        .itemStatus(ItemStatus.GOOD)
                        .startDateTime(LocalDateTime.of(2023,4,11,00,00,00))
                        .endDateTime(LocalDateTime.of(2023,4,20,00,00,00))
                        .build()

        );
        Page<AuctionEntity> page = new PageImpl<>(list);

        given(auctionRepository.findByUserEmail(any(), any())).willReturn(page);

        //when
        var result = auctionService.getAllAuctionListByUserEmail("user1", Pageable.ofSize(10));

        //then
        assertEquals(2, result.getTotalElements());
        assertEquals("item1", result.toList().get(0).getItemName());
        assertEquals(1111, result.toList().get(0).getStartingPrice());
        assertEquals(1000, result.toList().get(0).getMinimumBid());
        assertEquals(ItemStatus.BAD, result.toList().get(0).getItemStatus());
        assertEquals("item2", result.toList().get(1).getItemName());
        assertEquals(2222, result.toList().get(1).getStartingPrice());
        assertEquals(2000, result.toList().get(1).getMinimumBid());
        assertEquals(ItemStatus.GOOD, result.toList().get(1).getItemStatus());
    }

    @Test
    @DisplayName("해당 유저의 모든 경매 리스트 가져오기 - 실패")
    void getAllAuctionListByUserIdFail(){
        //given
        given(auctionRepository.findByUserEmail(any(), any())).willReturn(Page.empty());

        //when
        AuctionException exception = assertThrows(AuctionException.class,
                () -> auctionService.getAllAuctionListByUserEmail("user1", Pageable.ofSize(10)));

        //then
        assertEquals(NOT_FOUND_AUCTION_LIST, exception.getErrorCode());
    }


    @Test
    @DisplayName("경매 삭제 - 성공")
    void deleteAuction(){
        UserEntity user = UserEntity.builder()
                .userEmail("zerobase@gmail.com").build();
        AuctionEntity auction = AuctionEntity.builder()
                .auctionId(1L)
                .userEmail(user.getUserEmail())
                .itemName("item1")
                .startingPrice(1111)
                .minimumBid(1000)
                .transactionStatus(TransactionStatus.SALE)
                .itemStatus(ItemStatus.BAD)
                .startDateTime(LocalDateTime.of(2023,4,12,00,00,00))
                .endDateTime(LocalDateTime.of(2023,4,13,00,00,00))
                .build();
        given(auctionRepository.findByUserEmailAndAuctionId(any(), anyLong()))
                .willReturn(Optional.of(auction));
        ArgumentCaptor<AuctionEntity> captor = ArgumentCaptor.forClass(AuctionEntity.class);
        AuctionDto.Response auctionDto = auctionService.deleteAuction(1L, "zerobase@gmail.com");
        //then
        verify(auctionRepository, times(1)).delete(captor.capture());
        assertEquals("item1", auctionDto.getItemName());
        assertEquals(TransactionStatus.SALE, captor.getValue().getTransactionStatus());
        assertEquals(1000, captor.getValue().getMinimumBid());
    }

    @Test
    @DisplayName("경매 삭제 - 실패 - 이미 시작된 경매")
    void deleteSTARTEDAuctionFAIL(){
        UserEntity user = UserEntity.builder()
                .userEmail("zerobase@gmail.com").build();
        AuctionEntity auction = AuctionEntity.builder()
                .auctionId(1L)
                .userEmail(user.getUserEmail())
                .itemName("item1")
                .startingPrice(1111)
                .minimumBid(1000)
                .transactionStatus(TransactionStatus.SALE)
                .itemStatus(ItemStatus.BAD)
                .startDateTime(LocalDateTime.of(2023,4,1,00,00,00))
                .endDateTime(LocalDateTime.of(2023,4,13,00,00,00))
                .build();
        given(auctionRepository.findByUserEmailAndAuctionId(any(), anyLong()))
                .willReturn(Optional.of(auction));
        //then
        AuctionException exception = assertThrows(AuctionException.class,
                () -> auctionService.deleteAuction(1L, "zerobase@gmail.com"));

        //then
        assertEquals(UNABLE_DELETE_AUCTION, exception.getErrorCode());
    }

    @Test
    @DisplayName("경매 삭제 - 실패 - 유저가 등록한 경매가 아님")
    void deleteAnotherUsersAuctionFAIL(){

        given(auctionRepository.findByUserEmailAndAuctionId(any(), anyLong()))
                .willReturn(Optional.empty());
        //then
        AuctionException exception = assertThrows(AuctionException.class,
                () -> auctionService.deleteAuction(1L, "zerobase@gmail.com"));

        //then
        assertEquals(AUCTION_NOT_FOUND, exception.getErrorCode());
    }
}