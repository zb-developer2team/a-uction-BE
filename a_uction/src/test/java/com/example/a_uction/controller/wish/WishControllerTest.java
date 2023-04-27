package com.example.a_uction.controller.wish;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.model.wishList.dto.WishListResponse;
import com.example.a_uction.security.jwt.JwtProvider;
import com.example.a_uction.service.wish.WishService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.a_uction.exception.constants.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WishController.class)
class WishControllerTest {
    @MockBean
    private WishService wishService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;
    @MockBean
    private RedisTemplate<String, Object> redisTemplate;
    @MockBean
    private JwtProvider jwtProvider;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("관심 등록 - 성공")
    @WithMockUser
    void addWishSuccess() throws Exception {
        //given
        WishListResponse response = WishListResponse.builder()
                .id(1L)
                .userEmail("test@test.com")
                .itemName("test item")
                .build();

        given(wishService.addWishAuction(anyLong(),any())).willReturn(response);

        //when
        //then
        mockMvc.perform(post("/wish/" + 1L).with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.userEmail").value("test@test.com"))
                .andExpect(jsonPath("$.itemName").value("test item"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("관심 등록 - 실패")
    @WithMockUser
    void addWishFail() throws Exception {
        //given
        given(wishService.addWishAuction(anyLong(), any()))
                .willThrow(new AuctionException(ALREADY_EXIST_WISH_ITEM));

        //when
        //then
        mockMvc.perform(post("/wish/" + 1L).with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value("ALREADY_EXIST_WISH_ITEM"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원 관심 리스트 - 성공")
    @WithMockUser
    void getUserWishList() throws Exception {
        //given
        List<AuctionDto.Response> responseList = List.of(
                AuctionDto.Response.builder()
                        .auctionId(1L)
                        .description("this is test1")
                        .itemName("test1")
                        .minimumBid(100)
                        .startingPrice(1000)
                        .startDateTime(LocalDateTime.parse("2024-04-15T17:09:42.411"))
                        .endDateTime(LocalDateTime.parse("2024-04-15T17:10:42.411"))
                        .build(),
                AuctionDto.Response.builder()
                        .auctionId(2L)
                        .description("this is test2")
                        .itemName("test2")
                        .minimumBid(200)
                        .startingPrice(2000)
                        .startDateTime(LocalDateTime.parse("2024-04-15T17:09:42.411"))
                        .endDateTime(LocalDateTime.parse("2024-04-15T17:10:42.411"))
                        .build());

        Page<AuctionDto.Response> page = new PageImpl<>(responseList);

        given(wishService.getUserWishList(any(), any())).willReturn(page);

        //when
        //then
        mockMvc.perform(get("/wish/my-list").with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.content[0].auctionId").value(1))
                .andExpect(jsonPath("$.content[0].itemName").value("test1"))
                .andExpect(jsonPath("$.content[0].minimumBid").value(100))
                .andExpect(jsonPath("$.content[0].startingPrice").value(1000))
                .andExpect(jsonPath("$.content[0].description").value("this is test1"))
                .andExpect(jsonPath("$.content[1].auctionId").value(2))
                .andExpect(jsonPath("$.content[1].itemName").value("test2"))
                .andExpect(jsonPath("$.content[1].minimumBid").value(200))
                .andExpect(jsonPath("$.content[1].startingPrice").value(2000))
                .andExpect(jsonPath("$.content[1].description").value("this is test2"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원 관심 리스트 -실패")
    @WithMockUser
    void getUserWishListFail() throws Exception {
        //given
        given(wishService.getUserWishList(any(), any())).willThrow(new AuctionException(WISH_LIST_IS_EMPTY));

        //when
        //then
        mockMvc.perform(get("/wish/my-list").with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value("WISH_LIST_IS_EMPTY"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("경매에 관심 있는 유저 리스트 - 성공 - 경매 등록자")
    @WithMockUser
    void getWishUserListAboutAuctionSuccess() throws Exception{
        //given
        List<String> userList = List.of("test1", "test2", "test3");
        Page<String> userPage = new PageImpl<>(userList);

        given(wishService.getWishUserListAboutAuction(anyLong(), any(), any())).willReturn(userPage);

        //when
        //then
        mockMvc.perform(get("/wish/auction/" + 1L).with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.content[0]").value("test1"))
                .andExpect(jsonPath("$.content[1]").value("test2"))
                .andExpect(jsonPath("$.content[2]").value("test3"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("경매에 관심 있는 유저 리스트 - 성공 - 사용자")
    @WithMockUser
    void getWishUserListAboutAuctionSuccessUser() throws Exception{
        //given
        List<String> userList = List.of("3");
        Page<String> userPage = new PageImpl<>(userList);

        given(wishService.getWishUserListAboutAuction(anyLong(), any(), any())).willReturn(userPage);

        //when
        //then
        mockMvc.perform(get("/wish/auction/" + 1L).with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0]").value("3"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("경매에 관심 있는 유저 리스트 - 실패")
    @WithMockUser
    void getWishUserListAboutAuctionFail() throws Exception{
        //given
        given(wishService.getWishUserListAboutAuction(anyLong(), any(), any()))
                .willThrow(new AuctionException(NOT_EXIST_WISH_LIST_BY_USER));
        //when
        //then
        mockMvc.perform(get("/wish/auction/" + 1L).with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value("NOT_EXIST_WISH_LIST_BY_USER"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("관심등록 삭제 - 성공")
    @WithMockUser
    void deleteWishSuccess() throws Exception {
        //given
        WishListResponse response = WishListResponse.builder()
                .id(1L)
                .userEmail("test@test.com")
                .itemName("test item")
                .build();

        given(wishService.deleteWishAuction(anyLong(),any())).willReturn(response);
        //when
        //then
        mockMvc.perform(delete("/wish/" + 1L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.userEmail").value("test@test.com"))
                .andExpect(jsonPath("$.itemName").value("test item"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("관심등록 삭제 - 실패")
    @WithMockUser
    void deleteWishFail() throws Exception {
        //given
        given(wishService.deleteWishAuction(anyLong(),any()))
                .willThrow(new AuctionException(UNABLE_DELETE_WISH_LIST));

        //when
        //then
        mockMvc.perform(delete("/wish/" + 1L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value("UNABLE_DELETE_WISH_LIST"))
                .andExpect(status().isOk());
    }
}