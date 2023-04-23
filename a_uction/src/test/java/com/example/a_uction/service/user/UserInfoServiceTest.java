package com.example.a_uction.service.user;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.user.dto.InfoUser;
import com.example.a_uction.model.user.dto.ModifyUser;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import com.example.a_uction.service.s3.S3Service;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.a_uction.exception.constants.ErrorCode.ENTERED_THE_WRONG_PASSWORD;
import static com.example.a_uction.exception.constants.ErrorCode.USER_NOT_FOUND;
import static com.example.a_uction.exception.constants.ErrorCode.USER_PROFILE_IMAGE_IS_EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private UserInfoService userInfoService;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    private static final String TEST_IMAGE_SRC = "src/test/resources/image/test.png";

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
                .description(null)
                .build();

        ModifyUser.Request updateUser = ModifyUser.Request.builder()
                .currentPassword("1234")
                .phoneNumber("01043214321")
                .updatePassword("")
                .username("test1")
                .description("hello 나는야 경매왕!")
                .build();

        given(userRepository.findByUserEmail(any())).willReturn(Optional.ofNullable(userEntity));
        given(userRepository.save(any())).willReturn(userEntity);

        //when
        var result = userInfoService.modifyUserDetail("test@test.com", updateUser);

        //then
        assertEquals("test1", result.getUsername());
        assertEquals("01043214321", result.getPhoneNumber());
        assertEquals("hello 나는야 경매왕!", result.getDescription());
    }

    @Test
    @DisplayName("회원정보 수정 - 실패 - 사용자 정보 없음")
    void modifyUserDetailFailUser() {
        //given
        ModifyUser.Request updateUser = ModifyUser.Request.builder()
                .currentPassword("1234")
                .phoneNumber("01043214321")
                .updatePassword("")
                .username("test1")
                .description("hello 나는야 경매왕!")
                .build();

        given(userRepository.findByUserEmail(any())).willThrow(new AuctionException(USER_NOT_FOUND));

        //when
        AuctionException exception = assertThrows(AuctionException.class,
                () -> userInfoService.modifyUserDetail("test1@test.com", updateUser));

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
                .description(null)
                .build();

        ModifyUser.Request updateUser = ModifyUser.Request.builder()
                .currentPassword("4321")
                .phoneNumber("01043214321")
                .updatePassword("")
                .username("test1")
                .description("hello 나는야 경매왕!")
                .build();

        given(userRepository.findByUserEmail(any())).willReturn(Optional.ofNullable(userEntity));

        //when
        AuctionException exception = assertThrows(AuctionException.class,
                () -> userInfoService.modifyUserDetail("test1@test.com", updateUser));

        //then
        assertEquals(ENTERED_THE_WRONG_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("사용자 정보 보기 - 성공")
    void userInfoSuccess(){
        //given
        UserEntity user = UserEntity.builder()
                .username("test")
                .userEmail("test@test.com")
                .phoneNumber("01012345678")
                .id(1L)
                .createDateTime(LocalDateTime.of(2023,4,1,0,0,0))
                .description("hello 나는야 경매왕!")
                .build();

        given(userRepository.findByUserEmail(any())).willReturn(Optional.ofNullable(user));

        //when
        var result = userInfoService.userInfo("test@test.com");

        //then
        assertEquals("test", result.getUsername());
        assertEquals("test@test.com", result.getUserEmail());
        assertEquals("01012345678", result.getPhoneNumber());
        assertEquals("hello 나는야 경매왕!", result.getDescription());
    }

    @Test
    @DisplayName("사용자 정보 보기 - 실패")
    void userInfoFail(){
        //given
        given(userRepository.findByUserEmail(any())).willThrow(new AuctionException(USER_NOT_FOUND));

        //when
        AuctionException exception = assertThrows(AuctionException.class,
                () -> userInfoService.userInfo("test@test.com"));

        //then
        assertEquals(USER_NOT_FOUND, exception.getErrorCode());
    }
    @Test
    @DisplayName("프로필이미지 등록")
    void addProfileImage() throws IOException {
        //given
        UserEntity user = UserEntity
            .builder()
            .userEmail("test@test.com")
            .profileImageSrc(null)
            .build();
        given(userRepository.getByUserEmail(anyString()))
            .willReturn(user);
        given(s3Service.upload(any()))
            .willReturn("~/test/test.png");
        given(userRepository.save(any()))
            .willReturn(user);

        //when
        MockMultipartFile image = this.getMockMultipartFile("file", "image/png", TEST_IMAGE_SRC);
        InfoUser infoUser = userInfoService.modifyProfileImage(image, user.getUserEmail());

        //then
        assertEquals(infoUser.getProfileImageSrc(), "~/test/test.png");
    }
    @Test
    @DisplayName("프로필이미지 수정")
    void modifyProfileImage() throws IOException {
        //given
        UserEntity user = UserEntity
            .builder()
            .userEmail("test@test.com")
            .profileImageSrc("~/test/test.png")
            .build();
        given(userRepository.getByUserEmail(anyString()))
            .willReturn(user);
        given(s3Service.upload(any()))
            .willReturn("~/test/docker.png");
        given(userRepository.save(any()))
            .willReturn(user);

        //when
        MockMultipartFile image = this.getMockMultipartFile("file", "image/png", TEST_IMAGE_SRC);
        InfoUser infoUser = userInfoService.modifyProfileImage(image, user.getUserEmail());

        //then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(s3Service).getFileNameByUrl(captor.capture());
        assertEquals(captor.getValue(), "~/test/test.png");
        assertEquals(infoUser.getProfileImageSrc(), "~/test/docker.png");
    }
    @Test
    @DisplayName("프로필사진 삭제-성공")
    void deleteProfileImage_success () {
        //given
        UserEntity user = UserEntity
            .builder()
            .userEmail("test@test.com")
            .profileImageSrc("~test/test.png")
            .build();

        given(userRepository.getByUserEmail(anyString()))
            .willReturn(user);
        given(userRepository.save(any()))
            .willReturn(user);

        //when
        InfoUser infoUser = userInfoService.deleteProfileImage("test@test.com");

        //then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(s3Service).getFileNameByUrl(captor.capture());
        assertEquals(captor.getValue(), "~test/test.png");
        assertNull(infoUser.getProfileImageSrc());
    }
    @Test
    @DisplayName("프로필사진 삭제-실패")
    void deleteProfileImage_fail () {
        //given
        UserEntity user = UserEntity
            .builder()
            .userEmail("test@test.com")
            .profileImageSrc(null)
            .build();

        given(userRepository.getByUserEmail(anyString()))
            .willReturn(user);

        //when
        AuctionException exception =
            assertThrows(AuctionException.class,
                () -> userInfoService.deleteProfileImage("test@test.com"));

        //then
        assertEquals(USER_PROFILE_IMAGE_IS_EMPTY, exception.getErrorCode());
    }

    private MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path)
        throws IOException {
        FileInputStream fileInputStream = new FileInputStream(path);
        return new MockMultipartFile(fileName, fileName + "." + contentType, contentType,
            fileInputStream);
    }
}