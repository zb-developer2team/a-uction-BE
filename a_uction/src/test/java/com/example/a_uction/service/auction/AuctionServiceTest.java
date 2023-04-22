package com.example.a_uction.service.auction;

import static com.example.a_uction.exception.constants.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.auction.constants.AuctionStatus;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.constants.TransactionStatus;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import com.example.a_uction.model.file.entity.FileEntity;
import com.example.a_uction.model.user.entity.UserEntity;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.a_uction.model.user.repository.UserRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileService fileService;

    @InjectMocks
    private AuctionService auctionService;

    private static final String TEST_IMAGE_SRC = "src/test/resources/image/test.png";

    @Test
    @DisplayName("경매 등록 성공")
    void addAuction_SUCCESS() throws IOException {
        //given
        List<FileEntity> files = List.of(FileEntity.builder()
            .fileName("test")
            .src("test.png")
            .build());

        AuctionEntity entity = AuctionEntity.builder()
            .auctionId(1L)
            .itemName("test item")
            .itemStatus(ItemStatus.BAD)
            .startingPrice(2000)
            .minimumBid(200)
            .startDateTime(LocalDateTime.parse("2025-04-15T17:09:42.411"))
            .endDateTime(LocalDateTime.parse("2025-04-15T17:10:42.411"))
            .files(files)
            .build();

        given(auctionRepository.save(any()))
            .willReturn(entity);

        given(userRepository.getByUserEmail(any()))
            .willReturn(UserEntity.builder()
                .id(1L)
                .build());

        given(fileService.addFiles(any(), any()))
            .willReturn(entity);

        AuctionDto.Request request = AuctionDto.Request.builder()
            .itemName("test item2")
            .itemStatus(ItemStatus.GOOD)
            .startingPrice(1000)
            .minimumBid(100)
            .startDateTime(LocalDateTime.parse("2025-04-15T17:09:42.411"))
            .endDateTime(LocalDateTime.parse("2025-04-15T17:10:42.411"))
            .description("설명")
            .build();

        //when
        AuctionDto.Response response = auctionService.addAuction(request, this.getFiles(), "user1");
        //then
        assertEquals("test item", response.getItemName());
        assertEquals(ItemStatus.BAD, response.getItemStatus());
        assertEquals(2000, response.getStartingPrice());
        assertEquals(200, response.getMinimumBid());
        assertEquals(LocalDateTime.parse("2025-04-15T17:09:42.411"), response.getStartDateTime());
        assertEquals(LocalDateTime.parse("2025-04-15T17:10:42.411"), response.getEndDateTime());
        assertEquals("test.png", response.getFiles().get(0));
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
                    .build(), this.getFiles(), "user1"));
        //then
        assertEquals(new AuctionException(END_TIME_EARLIER_THAN_START_TIME).getMessage(),
            exception.getMessage());
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
                    .build(), this.getFiles(), "user1"));
        //then
        assertEquals(new AuctionException(BEFORE_START_TIME).getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("경매 아이디로 읽어오기")
    void getAuctionByAuctionIdTest() {
        //given
        List<FileEntity> files = List.of(FileEntity.builder()
            .fileName("test")
            .src("test.png")
            .build());

        given(auctionRepository.findById(anyLong()))
            .willReturn(Optional.of(AuctionEntity.builder()
                .itemName("test item")
                .itemStatus(ItemStatus.BAD)
                .startingPrice(2000)
                .minimumBid(200)
                .startDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
                .endDateTime(LocalDateTime.parse("2023-04-15T17:10:42.411"))
                .files(files)
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
    void getAuctionByAuctionId_NOTFOUND() {
        //given
        given(auctionRepository.findById(anyLong()))
            .willReturn(Optional.empty());
        //when
        AuctionException exception = assertThrows(AuctionException.class,
            () -> auctionService.getAuctionByAuctionId(1L));
        //then
        verify(auctionRepository, times(1)).findById(1L);
        assertEquals(new AuctionException(ErrorCode.AUCTION_NOT_FOUND).getMessage(),
            exception.getMessage());
    }

    @Test
    @DisplayName("경매 업데이트 성공")
    void updateAuctionSuccess() throws IOException {
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

        List<FileEntity> files = List.of(FileEntity.builder()
            .fileName("test")
            .src("test.png")
            .build());

        AuctionEntity auctionEntity = AuctionEntity.builder()
            .user(UserEntity.builder().id(1L).build())
            .auctionId(1L)
            .itemName("item")
            .startingPrice(1234)
            .minimumBid(1000)
            .transactionStatus(TransactionStatus.SALE)
            .itemStatus(ItemStatus.BAD)
            .startDateTime(LocalDateTime.of(2026, 5, 1, 0, 0, 0))
            .endDateTime(LocalDateTime.of(2026, 5, 2, 0, 0, 0))
            .files(files)
            .build();

        given(auctionRepository.findByUserIdAndAuctionId(any(), anyLong()))
            .willReturn(Optional.ofNullable(auctionEntity));
        given(auctionRepository.save(any())).willReturn(auctionEntity);
        given(userRepository.getByUserEmail(any()))
            .willReturn(UserEntity.builder()
                .id(1L)
                .build());
        // 추가
        given(fileService.updateFiles(any(), any()))
            .willReturn(auctionEntity);

        //when
        var result = auctionService.updateAuction(updateAuction, this.getFiles(), "user1", 1L);

        //then
        assertEquals("item2", result.getItemName());
        assertEquals(ItemStatus.GOOD, result.getItemStatus());
        assertEquals(startTime, result.getStartDateTime());
        assertEquals(endTime, result.getEndDateTime());
        assertEquals(2000, result.getMinimumBid());
        assertEquals(1000, result.getStartingPrice());
        assertEquals("test.png", result.getFiles().get(0));
    }

    @Test
    @DisplayName("경매 업데이트 실패 - 경매 시작 시간 확인")
    void updateAuctionFailStartTime() {
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
            .user(UserEntity.builder().id(1L).build())
            .itemName("item")
            .startingPrice(1234)
            .minimumBid(1000)
            .transactionStatus(TransactionStatus.SALE)
            .itemStatus(ItemStatus.BAD)
            .startDateTime(LocalDateTime.of(2025, 4, 1, 0, 0, 0))
            .endDateTime(LocalDateTime.of(2025, 4, 10, 0, 0, 0))
            .build();

        given(auctionRepository.findByUserIdAndAuctionId(any(), anyLong()))
            .willReturn(Optional.ofNullable(auctionEntity));
        given(userRepository.getByUserEmail(any()))
            .willReturn(UserEntity.builder()
                .id(1L)
                .build());

        //when
        AuctionException exception = assertThrows(AuctionException.class,
            () -> auctionService.updateAuction(updateAuction, this.getFiles(), "user1", 1L));

        //then
        assertEquals(BEFORE_START_TIME, exception.getErrorCode());
    }

    @Test
    @DisplayName("경매 업데이트 실패 - 경매 종료 시간 확인")
    void updateAuctionFailEndTime() {
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
            .user(UserEntity.builder().id(1L).build())
            .itemName("item")
            .startingPrice(1234)
            .minimumBid(1000)
            .transactionStatus(TransactionStatus.SALE)
            .itemStatus(ItemStatus.BAD)
            .startDateTime(LocalDateTime.of(2025, 4, 1, 0, 0, 0))
            .endDateTime(LocalDateTime.of(2025, 4, 10, 0, 0, 0))
            .build();

        given(auctionRepository.findByUserIdAndAuctionId(any(), anyLong()))
            .willReturn(Optional.ofNullable(auctionEntity));
        given(userRepository.getByUserEmail(any()))
            .willReturn(UserEntity.builder()
                .id(1L)
                .build());

        //when
        AuctionException exception = assertThrows(AuctionException.class,
            () -> auctionService.updateAuction(updateAuction, this.getFiles(), "user1", 1L));

        //then
        assertEquals(END_TIME_EARLIER_THAN_START_TIME, exception.getErrorCode());
    }

    @Test
    @DisplayName("해당 유저의 모든 경매 리스트 가져오기 - 성공")
    void getAllAuctionListByUserIdSuccess() {
        //given
        List<FileEntity> files = List.of(FileEntity.builder()
            .fileName("test")
            .src("test.png")
            .build());

        List<AuctionEntity> list = List.of(
            AuctionEntity.builder()
                .auctionId(1L)
                .user(UserEntity.builder().id(1L).build())
                .itemName("item1")
                .startingPrice(1111)
                .minimumBid(1000)
                .transactionStatus(TransactionStatus.SALE)
                .itemStatus(ItemStatus.BAD)
                .startDateTime(LocalDateTime.of(2025, 4, 1, 0, 0, 0))
                .endDateTime(LocalDateTime.of(2025, 4, 10, 0, 0, 0))
                .files(files)
                .build(),
            AuctionEntity.builder()
                .auctionId(2L)
                .user(UserEntity.builder().id(1L).build())
                .itemName("item2")
                .startingPrice(2222)
                .minimumBid(2000)
                .transactionStatus(TransactionStatus.SALE)
                .itemStatus(ItemStatus.GOOD)
                .startDateTime(LocalDateTime.of(2023, 4, 11, 0, 0, 0))
                .endDateTime(LocalDateTime.of(2023, 4, 20, 0, 0, 0))
                .files(files)
                .build()

        );
        Page<AuctionEntity> page = new PageImpl<>(list);

        given(auctionRepository.findByUserId(any(), any())).willReturn(page);
        given(userRepository.getByUserEmail(any()))
            .willReturn(UserEntity.builder()
                .id(1L)
                .build());

        //when
        var result = auctionService.getAllAuctionListByUserEmail("user1", Pageable.ofSize(10));

        //then
        assertEquals(2, result.getTotalElements());
        assertEquals("item1", result.toList().get(0).getItemName());
        assertEquals(1111, result.toList().get(0).getStartingPrice());
        assertEquals(1000, result.toList().get(0).getMinimumBid());
        assertEquals(ItemStatus.BAD, result.toList().get(0).getItemStatus());
        assertEquals("test.png", result.toList().get(0).getFiles().get(0));
        assertEquals("item2", result.toList().get(1).getItemName());
        assertEquals(2222, result.toList().get(1).getStartingPrice());
        assertEquals(2000, result.toList().get(1).getMinimumBid());
        assertEquals(ItemStatus.GOOD, result.toList().get(1).getItemStatus());
        assertEquals("test.png", result.toList().get(1).getFiles().get(0));
    }

    @Test
    @DisplayName("해당 유저의 모든 경매 리스트 가져오기 - 실패")
    void getAllAuctionListByUserIdFail() {
        //given
        given(auctionRepository.findByUserId(any(), any())).willReturn(Page.empty());
        given(userRepository.getByUserEmail(any()))
            .willReturn(UserEntity.builder()
                .id(1L)
                .build());

        //when
        AuctionException exception = assertThrows(AuctionException.class,
            () -> auctionService.getAllAuctionListByUserEmail("user1", Pageable.ofSize(10)));

        //then
        assertEquals(NOT_FOUND_AUCTION_LIST, exception.getErrorCode());
    }


    @Test
    @DisplayName("경매 삭제 - 성공")
    void deleteAuction() {
        UserEntity user = UserEntity.builder()
            .userEmail("zerobase@gmail.com").id(1L).build();
        List<FileEntity> files = List.of(FileEntity.builder()
            .fileName("test")
            .src("test.png")
            .build());

        AuctionEntity auction = AuctionEntity.builder()
            .auctionId(1L)
            .user(user)
            .itemName("item1")
            .startingPrice(1111)
            .minimumBid(1000)
            .transactionStatus(TransactionStatus.SALE)
            .itemStatus(ItemStatus.BAD)
            .startDateTime(LocalDateTime.of(2025, 4, 12, 0, 0, 0))
            .endDateTime(LocalDateTime.of(2025, 4, 13, 0, 0, 0))
            .files(files)
            .build();
        given(auctionRepository.findByUserIdAndAuctionId(any(), anyLong()))
            .willReturn(Optional.of(auction));
        given(userRepository.getByUserEmail(any()))
            .willReturn(UserEntity.builder()
                .id(1L)
                .build());
        ArgumentCaptor<AuctionEntity> captor = ArgumentCaptor.forClass(AuctionEntity.class);
        AuctionDto.Response auctionDto = auctionService.deleteAuction(1L, "zerobase@gmail.com");
        //then
        verify(auctionRepository, times(1)).delete(captor.capture());
        assertEquals("item1", auctionDto.getItemName());
        assertEquals(TransactionStatus.SALE, captor.getValue().getTransactionStatus());
        assertEquals(1000, captor.getValue().getMinimumBid());
        assertEquals("test.png", captor.getValue().getFiles().get(0).getSrc());
    }

    @Test
    @DisplayName("경매 삭제 - 실패 - 이미 시작된 경매")
    void deleteSTARTEDAuctionFAIL() {
        UserEntity user = UserEntity.builder()
            .userEmail("zerobase@gmail.com").id(1L).build();
        AuctionEntity auction = AuctionEntity.builder()
            .auctionId(1L)
            .user(user)
            .itemName("item1")
            .startingPrice(1111)
            .minimumBid(1000)
            .transactionStatus(TransactionStatus.SALE)
            .itemStatus(ItemStatus.BAD)
            .startDateTime(LocalDateTime.of(2023, 4, 1, 0, 0, 0))
            .endDateTime(LocalDateTime.of(2023, 4, 13, 0, 0, 0))
            .build();
        given(auctionRepository.findByUserIdAndAuctionId(any(), anyLong()))
            .willReturn(Optional.of(auction));
        given(userRepository.getByUserEmail(any()))
            .willReturn(UserEntity.builder()
                .id(1L)
                .build());
        //then
        AuctionException exception = assertThrows(AuctionException.class,
            () -> auctionService.deleteAuction(1L, "zerobase@gmail.com"));

        //then
        assertEquals(UNABLE_DELETE_AUCTION, exception.getErrorCode());
    }

    @Test
    @DisplayName("경매 삭제 - 실패 - 유저가 등록한 경매가 아님")
    void deleteAnotherUsersAuctionFAIL() {

        given(auctionRepository.findByUserIdAndAuctionId(any(), anyLong()))
            .willReturn(Optional.empty());
        given(userRepository.getByUserEmail(any()))
            .willReturn(UserEntity.builder()
                .id(1L)
                .build());
        //then
        AuctionException exception = assertThrows(AuctionException.class,
            () -> auctionService.deleteAuction(1L, "zerobase@gmail.com"));

        //then
        assertEquals(AUCTION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("상태별 경매 리스트 보기 - 성공 - 진행중")
    void getAllAuctionListByStatusSuccessProceeding() {
        //given
        List<FileEntity> files = List.of(FileEntity.builder()
            .fileName("test")
            .src("test.png")
            .build());
        List<AuctionEntity> list = List.of(
            AuctionEntity.builder()
                .auctionId(1L)
                .user(UserEntity.builder().id(1L).build())
                .itemName("item1")
                .startingPrice(1111)
                .minimumBid(1000)
                .transactionStatus(TransactionStatus.SALE)
                .itemStatus(ItemStatus.BAD)
                .startDateTime(LocalDateTime.of(2022, 4, 1, 0, 0, 0))
                .endDateTime(LocalDateTime.of(2025, 4, 10, 0, 0, 0))
                .files(files)
                .build(),
            AuctionEntity.builder()
                .auctionId(2L)
                .user(UserEntity.builder().id(1L).build())
                .itemName("item2")
                .startingPrice(2222)
                .minimumBid(2000)
                .transactionStatus(TransactionStatus.SALE)
                .itemStatus(ItemStatus.GOOD)
                .startDateTime(LocalDateTime.of(2022, 4, 11, 0, 0, 0))
                .endDateTime(LocalDateTime.of(2023, 4, 20, 0, 0, 0))
                .files(files)
                .build()
        );
        Page<AuctionEntity> page = new PageImpl<>(list);
        given(auctionRepository.findByStartDateTimeBeforeAndEndDateTimeAfter(
            any(), any(), any())).willReturn(page);
        //when
        var result = auctionService.getAllAuctionListByStatus(AuctionStatus.PROCEEDING,
            Pageable.ofSize(10));

        //then
        assertEquals(2, result.getTotalElements());
        assertEquals("item1", result.toList().get(0).getItemName());
        assertEquals(1111, result.toList().get(0).getStartingPrice());
        assertEquals(1000, result.toList().get(0).getMinimumBid());
        assertEquals(TransactionStatus.SALE, result.toList().get(0).getTransactionStatus());
        assertEquals(ItemStatus.BAD, result.toList().get(0).getItemStatus());
        assertEquals("test.png", result.toList().get(0).getFiles().get(0));
        assertEquals("item2", result.toList().get(1).getItemName());
        assertEquals(2222, result.toList().get(1).getStartingPrice());
        assertEquals(2000, result.toList().get(1).getMinimumBid());
        assertEquals(TransactionStatus.SALE, result.toList().get(1).getTransactionStatus());
        assertEquals(ItemStatus.GOOD, result.toList().get(1).getItemStatus());
        assertEquals("test.png", result.toList().get(1).getFiles().get(0));
    }

    @Test
    @DisplayName("상태별 경매 리스트 보기 - 성공 - 예정된")
    void getAllAuctionListByStatusSuccessScheduled() {
        //given
        List<FileEntity> files = List.of(FileEntity.builder()
            .fileName("test")
            .src("test.png")
            .build());

        List<AuctionEntity> list = List.of(
            AuctionEntity.builder()
                .auctionId(1L)
                .user(UserEntity.builder().id(1L).build())
                .itemName("item1")
                .startingPrice(1111)
                .minimumBid(1000)
                .transactionStatus(TransactionStatus.SALE)
                .itemStatus(ItemStatus.BAD)
                .startDateTime(LocalDateTime.of(2024, 4, 1, 0, 0, 0))
                .endDateTime(LocalDateTime.of(2025, 4, 10, 0, 0, 0))
                .files(files)
                .build(),
            AuctionEntity.builder()
                .auctionId(2L)
                .user(UserEntity.builder().id(1L).build())
                .itemName("item2")
                .startingPrice(2222)
                .minimumBid(2000)
                .transactionStatus(TransactionStatus.SALE)
                .itemStatus(ItemStatus.GOOD)
                .startDateTime(LocalDateTime.of(2024, 4, 11, 0, 0, 0))
                .endDateTime(LocalDateTime.of(2025, 4, 20, 0, 0, 0))
                .files(files)
                .build()
        );
        Page<AuctionEntity> page = new PageImpl<>(list);
        given(auctionRepository.findByStartDateTimeAfter(any(), any())).willReturn(page);
        //when
        var result = auctionService.getAllAuctionListByStatus(AuctionStatus.SCHEDULED,
            Pageable.ofSize(10));

        //then
        assertEquals(2, result.getTotalElements());
        assertEquals("item1", result.toList().get(0).getItemName());
        assertEquals(1111, result.toList().get(0).getStartingPrice());
        assertEquals(1000, result.toList().get(0).getMinimumBid());
        assertEquals(TransactionStatus.SALE, result.toList().get(0).getTransactionStatus());
        assertEquals(ItemStatus.BAD, result.toList().get(0).getItemStatus());
        assertEquals("test.png", result.toList().get(0).getFiles().get(0));
        assertEquals("item2", result.toList().get(1).getItemName());
        assertEquals(2222, result.toList().get(1).getStartingPrice());
        assertEquals(2000, result.toList().get(1).getMinimumBid());
        assertEquals(TransactionStatus.SALE, result.toList().get(1).getTransactionStatus());
        assertEquals(ItemStatus.GOOD, result.toList().get(1).getItemStatus());
        assertEquals("test.png", result.toList().get(1).getFiles().get(0));
    }

    @Test
    @DisplayName("상태별 경매 리스트 보기 - 성공 - 완료")
    void getAllAuctionListByStatusSuccessCompleted() {
        //given
        List<FileEntity> files = List.of(FileEntity.builder()
            .fileName("test")
            .src("test.png")
            .build());

        List<AuctionEntity> list = List.of(
            AuctionEntity.builder()
                .auctionId(1L)
                .user(UserEntity.builder().id(1L).build())
                .itemName("item1")
                .startingPrice(1111)
                .minimumBid(1000)
                .transactionStatus(TransactionStatus.SALE)
                .itemStatus(ItemStatus.BAD)
                .startDateTime(LocalDateTime.of(2021, 4, 1, 0, 0, 0))
                .endDateTime(LocalDateTime.of(2022, 4, 10, 0, 0, 0))
                .files(files)
                .build(),
            AuctionEntity.builder()
                .auctionId(2L)
                .user(UserEntity.builder().id(1L).build())
                .itemName("item2")
                .startingPrice(2222)
                .minimumBid(2000)
                .transactionStatus(TransactionStatus.SALE)
                .itemStatus(ItemStatus.GOOD)
                .startDateTime(LocalDateTime.of(2021, 4, 11, 0, 0, 0))
                .endDateTime(LocalDateTime.of(2022, 4, 20, 0, 0, 0))
                .files(files)
                .build()
        );
        Page<AuctionEntity> page = new PageImpl<>(list);
        given(auctionRepository.findByEndDateTimeBefore(any(), any())).willReturn(page);
        //when
        var result = auctionService.getAllAuctionListByStatus(AuctionStatus.COMPLETED,
            Pageable.ofSize(10));

        //then
        assertEquals(2, result.getTotalElements());
        assertEquals("item1", result.toList().get(0).getItemName());
        assertEquals(1111, result.toList().get(0).getStartingPrice());
        assertEquals(1000, result.toList().get(0).getMinimumBid());
        assertEquals(TransactionStatus.SALE, result.toList().get(0).getTransactionStatus());
        assertEquals(ItemStatus.BAD, result.toList().get(0).getItemStatus());
        assertEquals("test.png", result.toList().get(0).getFiles().get(0));
        assertEquals("item2", result.toList().get(1).getItemName());
        assertEquals(2222, result.toList().get(1).getStartingPrice());
        assertEquals(2000, result.toList().get(1).getMinimumBid());
        assertEquals(TransactionStatus.SALE, result.toList().get(1).getTransactionStatus());
        assertEquals(ItemStatus.GOOD, result.toList().get(1).getItemStatus());
        assertEquals("test.png", result.toList().get(1).getFiles().get(0));
    }

    @Test
    @DisplayName("상태별 경매 리스트 보기 - 실패 - 진행중")
    void getAllAuctionListByStatusFailProceeding() {
        //given
        given(auctionRepository.findByStartDateTimeBeforeAndEndDateTimeAfter(
            any(), any(), any())).willReturn(Page.empty());
        //when
        AuctionException exception = assertThrows(AuctionException.class,
            () -> auctionService.getAllAuctionListByStatus(AuctionStatus.PROCEEDING,
                Pageable.ofSize(10)));
        //then
        assertEquals(NOT_FOUND_AUCTION_STATUS_LIST, exception.getErrorCode());
    }

    @Test
    @DisplayName("상태별 경매 리스트 보기 - 실패 - 예정된")
    void getAllAuctionListByStatusFailScheduled() {
        //given
        given(auctionRepository.findByStartDateTimeAfter(
            any(), any())).willReturn(Page.empty());
        //when
        AuctionException exception = assertThrows(AuctionException.class,
            () -> auctionService.getAllAuctionListByStatus(AuctionStatus.SCHEDULED,
                Pageable.ofSize(10)));
        //then
        assertEquals(NOT_FOUND_AUCTION_STATUS_LIST, exception.getErrorCode());
    }

    @Test
    @DisplayName("상태별 경매 리스트 보기 - 실패 - 완료된")
    void getAllAuctionListByStatusFailCompleted() {
        //given
        given(auctionRepository.findByEndDateTimeBefore(
            any(), any())).willReturn(Page.empty());
        //when
        AuctionException exception = assertThrows(AuctionException.class,
            () -> auctionService.getAllAuctionListByStatus(AuctionStatus.COMPLETED,
                Pageable.ofSize(10)));
        //then
        assertEquals(NOT_FOUND_AUCTION_STATUS_LIST, exception.getErrorCode());
    }

    // 추가
    private List<MultipartFile> getFiles() throws IOException {
        List<MultipartFile> files = new ArrayList<>();
        MultipartFile file = getMockMultipartFile("test", "image/png",
            TEST_IMAGE_SRC);

        files.add(file);
        files.add(getMockMultipartFile("test2", "image/png",
            TEST_IMAGE_SRC));
        return files;
    }

    private MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path)
        throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        return new MockMultipartFile(fileName, fileName + "." + contentType, contentType,
            fileInputStream);
    }
}