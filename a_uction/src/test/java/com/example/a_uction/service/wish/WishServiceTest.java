package com.example.a_uction.service.wish;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import com.example.a_uction.model.wishList.entity.WishListEntity;
import com.example.a_uction.model.wishList.repository.WishRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.a_uction.exception.constants.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WishServiceTest {
    @InjectMocks
    private WishService wishService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuctionRepository auctionRepository;
    @Mock
    private WishRepository wishRepository;



    @Test
    @DisplayName("관심 등록 - 성공")
    void addWishAuctionSuccess() {
        //given
        UserEntity user = UserEntity.builder()
                .id(1L)
                .userEmail("test@test.com")
                .build();

        AuctionEntity auction = AuctionEntity.builder()
                .auctionId(1L)
                .itemName("test item")
                .itemStatus(ItemStatus.BAD)
                .startingPrice(2000)
                .minimumBid(200)
                .startDateTime(LocalDateTime.parse("2025-04-15T17:09:42.411"))
                .endDateTime(LocalDateTime.parse("2025-04-15T17:10:42.411"))
                .description("this is test")
                .build();

        given(userRepository.getByUserEmail(any())).willReturn(user);
        given(wishRepository.findByWishUserIdAndWishAuctionAuctionId(any(), any()))
                .willReturn(Optional.empty());
        given(wishRepository.save(any())).willReturn(WishListEntity.builder()
                    .id(1L)
                    .wishAuction(auction)
                    .wishUser(user)
                .build());

        //when

        var result = wishService.addWishAuction(1L, "test@test.com");

        //then
        assertEquals(1L, result.getId());
        assertEquals("test@test.com", result.getUserEmail());
        assertEquals("test item", result.getItemName());
    }

    @Test
    @DisplayName("관심 등록 - 실패")
    void addWishAuctionFail() {
        //given
        UserEntity user = UserEntity.builder()
                .id(1L)
                .userEmail("test@test.com")
                .build();

        AuctionEntity auction = AuctionEntity.builder()
                .auctionId(1L)
                .itemName("test item")
                .itemStatus(ItemStatus.BAD)
                .startingPrice(2000)
                .minimumBid(200)
                .startDateTime(LocalDateTime.parse("2025-04-15T17:09:42.411"))
                .endDateTime(LocalDateTime.parse("2025-04-15T17:10:42.411"))
                .description("this is test")
                .build();

        given(userRepository.getByUserEmail(any())).willReturn(user);
        given(wishRepository.findByWishUserIdAndWishAuctionAuctionId(any(), any()))
                .willReturn(Optional.ofNullable(WishListEntity.builder()
                        .id(1L)
                        .wishAuction(auction)
                        .wishUser(user)
                .build()));

        //when
        AuctionException exception = assertThrows(AuctionException.class,
                () -> wishService.addWishAuction(1L, "test@test.com"));

        //then
        assertEquals(ALREADY_EXIST_WISH_ITEM, exception.getErrorCode());
    }

    @Test
    @DisplayName("고객의 관심리스트 가져오기 - 성공")
    void getUserWishListSuccess() {
        //given
        UserEntity user = UserEntity.builder()
                .id(1L)
                .userEmail("test@test.com")
                .wishList(List.of(WishListEntity.builder()
                        .wishAuction(AuctionEntity.builder()
                                .auctionId(1L)
                                .itemName("test item")
                                .itemStatus(ItemStatus.BAD)
                                .startingPrice(2000)
                                .minimumBid(200)
                                .startDateTime(LocalDateTime.parse("2025-04-15T17:09:42.411"))
                                .endDateTime(LocalDateTime.parse("2025-04-15T17:10:42.411"))
                                .description("this is test")
                                .build())
                        .build(), WishListEntity.builder()
                        .wishAuction(AuctionEntity.builder()
                                .auctionId(2L)
                                .itemName("test item2")
                                .itemStatus(ItemStatus.GOOD)
                                .startingPrice(2000)
                                .minimumBid(200)
                                .startDateTime(LocalDateTime.parse("2025-04-15T17:09:42.411"))
                                .endDateTime(LocalDateTime.parse("2025-04-15T17:10:42.411"))
                                .description("this is test2")
                                .build())
                        .build()))
                .build();

        given(userRepository.getByUserEmail(any())).willReturn(user);


        //when
        var result = wishService.getUserWishList("test@test.com", Pageable.ofSize(10));

        //then
        assertEquals(1L, result.toList().get(0).getAuctionId());
        assertEquals("test item", result.toList().get(0).getItemName());
        assertEquals("this is test", result.toList().get(0).getDescription());
        assertEquals(2000, result.toList().get(0).getStartingPrice());
        assertEquals(2L, result.toList().get(1).getAuctionId());
        assertEquals("test item2", result.toList().get(1).getItemName());
        assertEquals("this is test2", result.toList().get(1).getDescription());
        assertEquals(2000, result.toList().get(1).getStartingPrice());

    }

    @Test
    @DisplayName("고객의 관심리스트 가져오기 - 실패")
    void getUserWishListFail() {
        //given
        UserEntity user = UserEntity.builder()
                .id(1L)
                .userEmail("test@test.com")
                .wishList(new ArrayList<>())
                .build();

        given(userRepository.getByUserEmail(any())).willReturn(user);

        //when
        AuctionException exception = assertThrows(AuctionException.class,
                () -> wishService.getUserWishList("test@test.com", Pageable.ofSize(10)));

        //then
        assertEquals(WISH_LIST_IS_EMPTY, exception.getErrorCode());
    }

    @Test
    @DisplayName("경매에 관심있는 사용자의 리스트 가져오기 - 성공 - 경매 등록자")
    void getWishUserListAboutAuctionSuccess() {
        //given
        AuctionEntity auction = AuctionEntity.builder()
                .auctionId(1L)
                .user(UserEntity.builder().id(1L).userEmail("admin").build())
                .itemName("test item")
                .itemStatus(ItemStatus.BAD)
                .startingPrice(2000)
                .minimumBid(200)
                .startDateTime(LocalDateTime.parse("2025-04-15T17:09:42.411"))
                .endDateTime(LocalDateTime.parse("2025-04-15T17:10:42.411"))
                .description("this is test")
                .wishListForUser(List.of(
                        WishListEntity.builder()
                                        .wishUser(UserEntity.builder()
                                                .id(1L).userEmail("test1").build()).build(),
                        WishListEntity.builder()
                                .wishUser(UserEntity.builder()
                                        .id(2L).userEmail("test2").build()).build()))
                .build();

        given(auctionRepository.getByAuctionId(any())).willReturn(auction);

        //when
        var result = wishService.getWishUserListAboutAuction(1L, "admin",
                Pageable.ofSize(10));

        //then
        assertEquals(2, result.toList().size());
        assertEquals("test1", result.toList().get(0));
        assertEquals("test2", result.toList().get(1));
    }

    @Test
    @DisplayName("경매에 관심있는 사용자의 리스트 가져오기 - 성공 - 사용자")
    void getWishUserListAboutAuctionSuccessUser() {
        //given
        AuctionEntity auction = AuctionEntity.builder()
                .auctionId(1L)
                .user(UserEntity.builder().id(1L).userEmail("admin").build())
                .itemName("test item")
                .itemStatus(ItemStatus.BAD)
                .startingPrice(2000)
                .minimumBid(200)
                .startDateTime(LocalDateTime.parse("2025-04-15T17:09:42.411"))
                .endDateTime(LocalDateTime.parse("2025-04-15T17:10:42.411"))
                .description("this is test")
                .wishListForUser(List.of(
                        WishListEntity.builder()
                                .wishUser(UserEntity.builder()
                                        .id(1L).userEmail("test1").build()).build(),
                        WishListEntity.builder()
                                .wishUser(UserEntity.builder()
                                        .id(2L).userEmail("test2").build()).build()))
                .build();

        given(auctionRepository.getByAuctionId(any())).willReturn(auction);

        //when
        var result = wishService.getWishUserListAboutAuction(1L, "user",
                Pageable.ofSize(10));

        //then
        assertEquals(1, result.toList().size());
        assertEquals("2", result.toList().get(0));
    }

    @Test
    @DisplayName("경매에 관심있는 사용자의 리스트 가져오기 - 실패")
    void getWishUserListAboutAuctionFail() {
        //given
        AuctionEntity auction = AuctionEntity.builder()
                .auctionId(1L)
                .user(UserEntity.builder().id(1L).userEmail("admin").build())
                .itemName("test item")
                .itemStatus(ItemStatus.BAD)
                .startingPrice(2000)
                .minimumBid(200)
                .startDateTime(LocalDateTime.parse("2025-04-15T17:09:42.411"))
                .endDateTime(LocalDateTime.parse("2025-04-15T17:10:42.411"))
                .description("this is test")
                .wishListForUser(new ArrayList<>())
                .build();

        given(auctionRepository.getByAuctionId(any())).willReturn(auction);
        //when
        AuctionException exception = assertThrows(AuctionException.class,
                () -> wishService.getWishUserListAboutAuction(1L, "admin", Pageable.ofSize(10)));

        //then
        assertEquals(NOT_EXIST_WISH_LIST_BY_USER, exception.getErrorCode());
    }

    @Test
    @DisplayName("관심등록 삭제 - 성공")
    void deleteWishAuctionSuccess() {
        //given
        UserEntity user = UserEntity.builder()
                .id(1L)
                .userEmail("test@test.com")
                .build();

        AuctionEntity auction = AuctionEntity.builder()
                .auctionId(1L)
                .user(UserEntity.builder().id(1L).userEmail("admin").build())
                .itemName("test item")
                .itemStatus(ItemStatus.BAD)
                .startingPrice(2000)
                .minimumBid(200)
                .startDateTime(LocalDateTime.parse("2025-04-15T17:09:42.411"))
                .endDateTime(LocalDateTime.parse("2025-04-15T17:10:42.411"))
                .description("this is test")
                .wishListForUser(new ArrayList<>())
                .build();

        WishListEntity wishList = WishListEntity.builder()
                .id(1L)
                .wishUser(user)
                .wishAuction(auction)
                .build();

        given(userRepository.getByUserEmail(any())).willReturn(user);
        given(wishRepository.findByWishUserIdAndWishAuctionAuctionId(any(), any()))
                .willReturn(Optional.ofNullable(wishList));

        //when
        var result = wishService.deleteWishAuction(1L, "test@test.com");

        //then
        assertEquals(1L, result.getId());
        assertEquals("test item", result.getItemName());
    }

    @Test
    @DisplayName("관심등록 삭제 - 실패")
    void deleteWishAuctionFail() {
        //given
        UserEntity user = UserEntity.builder()
                .id(1L)
                .userEmail("test@test.com")
                .build();

        given(userRepository.getByUserEmail(any())).willReturn(user);
        given(wishRepository.findByWishUserIdAndWishAuctionAuctionId(any(), any()))
                .willThrow(new AuctionException(UNABLE_DELETE_WISH_LIST));
        //when
        AuctionException exception = assertThrows(AuctionException.class,
                () -> wishService.deleteWishAuction(1L, "test@test.com"));

        //then
        assertEquals(UNABLE_DELETE_WISH_LIST, exception.getErrorCode());
    }
}