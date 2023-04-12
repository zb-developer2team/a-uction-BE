package com.example.a_uction.controller.user;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.user.dto.InfoUser;
import com.example.a_uction.model.user.dto.ModifyUser;
import com.example.a_uction.security.jwt.JwtProvider;
import com.example.a_uction.service.user.UserInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.a_uction.exception.constants.ErrorCode.ENTERED_THE_WRONG_PASSWORD;
import static com.example.a_uction.exception.constants.ErrorCode.USER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserInfoController.class)
class UserInfoControllerTest {

    @MockBean
    private UserInfoService userInfoService;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;
    @MockBean
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("회원정보 수정 - 성공")
    void modifySuccess() throws Exception {
        //given
        ModifyUser.Request updateUser = ModifyUser.Request.builder()
                .currentPassword("4321")
                .phoneNumber("01043214321")
                .updatePassword("")
                .username("test1")
                .build();

        ModifyUser.Response response = ModifyUser.Response.builder()
                .username("test1")
                .phoneNumber("01043214321")
                .build();

        given(userInfoService.modifyUserDetail(any(), any())).willReturn(response);

        //when
        //then
        mockMvc.perform(put("/users/detail/modify").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(jsonPath("$.username").value("test1"))
                .andExpect(jsonPath("$.phoneNumber").value("01043214321"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("회원정보 수정 - 실패 - 사용자 없음")
    void modifyFailUser() throws Exception {
        //given
        ModifyUser.Request updateUser = ModifyUser.Request.builder()
                .currentPassword("4321")
                .phoneNumber("01043214321")
                .updatePassword("")
                .username("test1")
                .build();

        given(userInfoService.modifyUserDetail(any(), any())).willThrow(new AuctionException(USER_NOT_FOUND));

        //when
        //then
        mockMvc.perform(put("/users/detail/modify").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser
    @DisplayName("회원정보 수정 - 실패 - 비밀번호 일치하지 않음")
    void modifyFailPassword() throws Exception {
        //given
        ModifyUser.Request updateUser = ModifyUser.Request.builder()
                .currentPassword("4321")
                .phoneNumber("01043214321")
                .updatePassword("")
                .username("test1")
                .build();


        given(userInfoService.modifyUserDetail(any(), any())).willThrow(new AuctionException(ENTERED_THE_WRONG_PASSWORD));

        //when
        //then
        mockMvc.perform(put("/users/detail/modify").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(jsonPath("$.errorCode").value("ENTERED_THE_WRONG_PASSWORD"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("회원정보 보기 - 성공")
    void userInfoSuccess() throws Exception {
        //given
        InfoUser infoUser = InfoUser.builder()
                .username("test")
                .userEmail("test@test.com")
                .phoneNumber("01012345678")
                .build();

        given(userInfoService.userInfo(any())).willReturn(infoUser);
        //when
        //then
        mockMvc.perform(get("/users/detail").with(csrf()))
                .andExpect(jsonPath("$.username").value("test"))
                .andExpect(jsonPath("$.userEmail").value("test@test.com"))
                .andExpect(jsonPath("$.phoneNumber").value("01012345678"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("회원정보 보기 - 실패")
    void userInfoFail() throws Exception {
        //given
        given(userInfoService.userInfo(any())).willThrow(new AuctionException(USER_NOT_FOUND));
        //when
        //then
        mockMvc.perform(get("/users/detail").with(csrf()))
                .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"))
                .andExpect(status().isOk());
    }
}