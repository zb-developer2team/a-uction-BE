package com.example.a_uction.service;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.user.dto.ModifyUser;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.a_uction.exception.constants.ErrorCode.ENTERED_THE_WRONG_PASSWORD;
import static com.example.a_uction.exception.constants.ErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserModifyServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserModifyService userModifyService;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;


    @Test
    @DisplayName("회원정보 수정 - 성공")
    void modifyUserDetailSuccess() {
        //given
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .userEmail("test@test.com")
                .username("test")
                .createDateTime(LocalDateTime.now())
                .phoneNumber("01012341234")
                .password(passwordEncoder.encode("1234"))
                .build();

        ModifyUser.Request updateUser = ModifyUser.Request.builder()
                .currentPassword("1234")
                .phone("01043214321")
                .updatePassword("")
                .username("test1")
                .build();

        given(userRepository.findByUserEmail(any())).willReturn(Optional.ofNullable(userEntity));
        given(userRepository.save(any())).willReturn(userEntity);

        //when
        var result = userModifyService.modifyUserDetail("test@test.com", updateUser);

        //then
        assertEquals("test1", result.getUsername());
        assertEquals("01043214321", result.getPhone());
    }

    @Test
    @DisplayName("회원정보 수정 - 실패 - 사용자 정보 없음")
    void modifyUserDetailFailUser() {
        //given
        ModifyUser.Request updateUser = ModifyUser.Request.builder()
                .currentPassword("1234")
                .phone("01043214321")
                .updatePassword("")
                .username("test1")
                .build();

        given(userRepository.findByUserEmail(any())).willThrow(new AuctionException(USER_NOT_FOUND));

        //when
        AuctionException exception = assertThrows(AuctionException.class,
                () -> userModifyService.modifyUserDetail("test1@test.com", updateUser));

        //then
        assertEquals(USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원정보 수정 - 실패 - 현재 비밀번호 일치하지 않음")
    void modifyUserDetailFailPassword() {
        //given
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .userEmail("test@test.com")
                .username("test")
                .createDateTime(LocalDateTime.now())
                .phoneNumber("01012341234")
                .password(passwordEncoder.encode("1234"))
                .build();

        ModifyUser.Request updateUser = ModifyUser.Request.builder()
                .currentPassword("4321")
                .phone("01043214321")
                .updatePassword("")
                .username("test1")
                .build();

        given(userRepository.findByUserEmail(any())).willReturn(Optional.ofNullable(userEntity));

        //when
        AuctionException exception = assertThrows(AuctionException.class,
                () -> userModifyService.modifyUserDetail("test1@test.com", updateUser));

        //then
        assertEquals(ENTERED_THE_WRONG_PASSWORD, exception.getErrorCode());
    }
}