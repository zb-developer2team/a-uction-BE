package com.example.a_uction.controller;

import com.example.a_uction.model.auction.constants.AuctionStatus;
import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.service.AuctionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuctionController.class)
class AuctionControllerTest {

    @MockBean
    private AuctionService auctionService;

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

        given(auctionService.addAuction(any()))
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
}