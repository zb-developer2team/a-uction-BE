package com.example.a_uction.controller;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.auction.constants.AuctionStatus;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.security.jwt.JwtProvider;
import com.example.a_uction.service.AuctionService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuctionController.class)
class AuctionControllerTest {

    @MockBean
    private AuctionService auctionService;

    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;
    @MockBean
    private RedisTemplate redisTemplate;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("경매 생성 성공")
    @WithMockUser
    void addAuctionSuccess() throws Exception {
        AuctionDto.Response auctionDto = AuctionDto.Response.builder()
                .itemName("test item2")
                .itemStatus(ItemStatus.GOOD)
                .startingPrice(2000)
                .minimumBid(200)
                .auctionStatus(AuctionStatus.SCHEDULED)
                .startDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
                .endDateTime(LocalDateTime.parse("2023-04-15T17:10:42.411"))
                .build();

        given(auctionService.addAuction(any(), any()))
                .willReturn(auctionDto);
        mockMvc.perform(post("/auction")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                AuctionEntity.builder()
                                .itemName("test item")
                                .itemStatus(ItemStatus.BAD)
                                .startingPrice(2000)
                                .minimumBid(200)
                                .startDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
                                .endDateTime(LocalDateTime.parse("2023-04-15T17:10:42.411"))
                                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auctionStatus").value(AuctionStatus.SCHEDULED.name()))
                .andExpect(jsonPath("$.itemName").value("test item2"))
                .andDo(print());
    }


    @Test
    @WithMockUser
    @DisplayName("경매 수정 - 성공")
    void updateAuctionSuccess() throws Exception {
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
                .userEmail("user1")
                .itemName("item2")
                .startingPrice(1000)
                .minimumBid(2000)
                .itemStatus(ItemStatus.GOOD)
                .startDateTime(startTime)
                .endDateTime(endTime)
                .build();

        given(auctionService.updateAuction(any(AuctionDto.Request.class), any(), anyLong()))
                .willReturn(new AuctionDto.Response().fromEntity(auctionEntity));
        //when
        //then
        mockMvc.perform(put("/auction").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("auctionId", "1")
                        .content(objectMapper.writeValueAsString(updateAuction)))
                .andExpect(jsonPath("$.itemName").value("item2"))
                .andExpect(jsonPath("$.itemStatus").value("GOOD"))
                .andExpect(jsonPath("$.minimumBid").value("2000"))
                .andExpect(jsonPath("$.startingPrice").value("1000"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("경매 수정 - 실패 - 해당 경매 없음")
    void updateAuctionFail() throws Exception {
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

        given(auctionService.updateAuction(any(AuctionDto.Request.class), any(), anyLong()))
                .willThrow(new AuctionException(ErrorCode.INTERNAL_SERVER_ERROR));
        //when
        //then
        mockMvc.perform(put("/auction").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("auctionId", "1")
                        .content(objectMapper.writeValueAsString(updateAuction)))
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("경매 수정 - 실패 - 시작시간이 등록시간 이전")
    void updateAuctionFailStartTime() throws Exception {
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

        given(auctionService.updateAuction(any(AuctionDto.Request.class), any(), anyLong()))
                .willThrow(new AuctionException(ErrorCode.BEFORE_START_TIME));
        //when
        //then
        mockMvc.perform(put("/auction").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("auctionId", "1")
                        .content(objectMapper.writeValueAsString(updateAuction)))
                .andExpect(jsonPath("$.errorCode").value("BEFORE_START_TIME"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("경매 수정 - 실패 - 시작시간이 등록시간 이전")
    void updateAuctionFailEndTime() throws Exception {
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

        given(auctionService.updateAuction(any(AuctionDto.Request.class), any(), anyLong()))
            .willThrow(new AuctionException(ErrorCode.END_TIME_EARLIER_THAN_START_TIME));
        //when
        //then
        mockMvc.perform(put("/auction").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("auctionId", "1")
                .content(objectMapper.writeValueAsString(updateAuction)))
            .andExpect(jsonPath("$.errorCode").value("END_TIME_EARLIER_THAN_START_TIME"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("경매 읽기 성공")
    @WithMockUser
    void getAuctionByAuctionId_SUCCESS() throws Exception {
        AuctionDto.Response auctionDto = AuctionDto.Response.builder()
            .itemName("test item2")
            .itemStatus(ItemStatus.GOOD)
            .startingPrice(2000)
            .minimumBid(200)
            .auctionStatus(AuctionStatus.SCHEDULED)
            .startDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
            .endDateTime(LocalDateTime.parse("2023-04-15T17:10:42.411"))
            .build();
        given(auctionService.getAuctionByAuctionId(anyLong()))
            .willReturn(auctionDto);
        mockMvc.perform(get("/auction/" + 1L)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.auctionStatus").value(AuctionStatus.SCHEDULED.name()))
            .andExpect(jsonPath("$.itemName").value("test item2"))
            .andExpect(jsonPath("$.itemStatus").value(ItemStatus.GOOD.name()))
            .andExpect(jsonPath("$.startingPrice").value(2000))
            .andExpect(jsonPath("$.minimumBid").value(200))
            .andExpect(jsonPath("$.auctionStatus").value(AuctionStatus.SCHEDULED.name()))
            .andExpect(jsonPath("$.startDateTime").value("2023-04-15T17:09:42.411"))
            .andExpect(jsonPath("$.endDateTime").value("2023-04-15T17:10:42.411"));
    }

    @Test
    @DisplayName("user 의 모든 경매 리스트 가져오기 - 성공")
    @WithMockUser
    void getAllAuctionListByUserIdSuccess() throws Exception {
        //given
        List<AuctionDto.Response> list = List.of(
                AuctionDto.Response.builder()
                        .itemName("item1")
                        .itemStatus(ItemStatus.GOOD)
                        .startingPrice(1000)
                        .minimumBid(100)
                        .auctionStatus(AuctionStatus.SCHEDULED)
                        .startDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
                        .endDateTime(LocalDateTime.parse("2023-04-15T17:10:42.411"))
                        .build(),
                AuctionDto.Response.builder()
                        .itemName("item2")
                        .itemStatus(ItemStatus.BAD)
                        .startingPrice(2000)
                        .minimumBid(200)
                        .auctionStatus(AuctionStatus.COMPLETE)
                        .startDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
                        .endDateTime(LocalDateTime.parse("2023-04-15T17:10:42.411"))
                        .build()
        );

        Page<AuctionDto.Response> page = new PageImpl<>(list);

        given(auctionService.getAllAuctionListByUserEmail(any(), any())).willReturn(page);

        //when
        //then
        mockMvc.perform(get("/auction/read").with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.content[0].itemName").value("item1"))
                .andExpect(jsonPath("$.content[0].itemStatus").value("GOOD"))
                .andExpect(jsonPath("$.content[0].auctionStatus").value("SCHEDULED"))
                .andExpect(jsonPath("$.content[0].startingPrice").value(1000))
                .andExpect(jsonPath("$.content[0].minimumBid").value(100))
                .andExpect(jsonPath("$.content[1].itemName").value("item2"))
                .andExpect(jsonPath("$.content[1].itemStatus").value("BAD"))
                .andExpect(jsonPath("$.content[1].auctionStatus").value("COMPLETE"))
                .andExpect(jsonPath("$.content[1].startingPrice").value(2000))
                .andExpect(jsonPath("$.content[1].minimumBid").value(200))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("user 의 모든 경매 리스트 가져오기 - 실패")
    @WithMockUser
    void getAllAuctionListByUserIdFail() throws Exception {
        //given
        given(auctionService.getAllAuctionListByUserEmail(any(),any()))
                .willThrow(new AuctionException(NOT_FOUND_AUCTION_LIST));
        //when
        //then
        mockMvc.perform(get("/auction/read").with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND_AUCTION_LIST"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("user 의 상태별 경매 리스트 가져오기 - 성공")
    @WithMockUser
    void getAuctionListByUserIdAndStatusSuccess() throws Exception {
        //given
        List<AuctionDto.Response> list = List.of(
                AuctionDto.Response.builder()
                        .itemName("item1")
                        .itemStatus(ItemStatus.GOOD)
                        .startingPrice(1000)
                        .minimumBid(100)
                        .auctionStatus(AuctionStatus.SCHEDULED)
                        .startDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
                        .endDateTime(LocalDateTime.parse("2023-04-15T17:10:42.411"))
                        .build(),
                AuctionDto.Response.builder()
                        .itemName("item2")
                        .itemStatus(ItemStatus.BAD)
                        .startingPrice(2000)
                        .minimumBid(200)
                        .auctionStatus(AuctionStatus.SCHEDULED)
                        .startDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
                        .endDateTime(LocalDateTime.parse("2023-04-15T17:10:42.411"))
                        .build()
        );

        Page<AuctionDto.Response> page = new PageImpl<>(list);

        given(auctionService.getAuctionListByUserEmailAndAuctionStatus(any(), any(), any())).willReturn(page);

        //when
        //then
        mockMvc.perform(get("/auction/read/SCHEDULED").with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.content[0].itemName").value("item1"))
                .andExpect(jsonPath("$.content[0].itemStatus").value("GOOD"))
                .andExpect(jsonPath("$.content[0].auctionStatus").value("SCHEDULED"))
                .andExpect(jsonPath("$.content[0].startingPrice").value(1000))
                .andExpect(jsonPath("$.content[0].minimumBid").value(100))
                .andExpect(jsonPath("$.content[1].itemName").value("item2"))
                .andExpect(jsonPath("$.content[1].itemStatus").value("BAD"))
                .andExpect(jsonPath("$.content[1].auctionStatus").value("SCHEDULED"))
                .andExpect(jsonPath("$.content[1].startingPrice").value(2000))
                .andExpect(jsonPath("$.content[1].minimumBid").value(200))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("user 의 상태별 경매 리스트 가져오기 - 실패")
    @WithMockUser
    void getAuctionListByUserIdAndStatusFail() throws Exception {
        //given
        given(auctionService.getAuctionListByUserEmailAndAuctionStatus(any(),any(),any()))
                .willThrow(new AuctionException(AUCTION_NOT_FOUND));
        //when
        //then
        mockMvc.perform(get("/auction/read/SCHEDULED").with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value("AUCTION_NOT_FOUND"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저의 경매 삭제 - 성공")
    @WithMockUser
    void deleteAuctionSuccess() throws Exception{
        AuctionDto.Response auctionDto = AuctionDto.Response.builder()
                .itemName("test item2")
                .itemStatus(ItemStatus.GOOD)
                .startingPrice(2000)
                .minimumBid(200)
                .auctionStatus(AuctionStatus.SCHEDULED)
                .startDateTime(LocalDateTime.parse("2023-04-15T17:09:42.411"))
                .endDateTime(LocalDateTime.parse("2023-04-15T17:10:42.411"))
                .build();
        given(auctionService.deleteAuction(anyLong(), anyString()))
                .willReturn(auctionDto);
        mockMvc.perform(delete("/auction/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auctionStatus").value(AuctionStatus.SCHEDULED.name()))
                .andExpect(jsonPath("$.itemName").value("test item2"))
                .andDo(print());
    }

    @Test
    @DisplayName("다른 유저의 경매 삭제 - 실패")
    @WithMockUser
    void deleteAuctionFail() throws Exception{
        given(auctionService.deleteAuction(anyLong(), anyString()))
                .willThrow(new AuctionException(AUCTION_NOT_FOUND));
        mockMvc.perform(delete("/auction/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value("AUCTION_NOT_FOUND"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("이미 시작된 경매 삭제 - 실패")
    @WithMockUser
    void deleteStartedAuctionFail() throws Exception{
        given(auctionService.deleteAuction(anyLong(), anyString()))
                .willThrow(new AuctionException(UNABLE_DELETE_AUCTION));
        mockMvc.perform(delete("/auction/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value("UNABLE_DELETE_AUCTION"))
                .andExpect(status().isOk());
    }
}
