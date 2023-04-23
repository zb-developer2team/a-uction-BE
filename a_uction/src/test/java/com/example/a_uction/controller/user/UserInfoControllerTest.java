package com.example.a_uction.controller.user;

import static com.example.a_uction.exception.constants.ErrorCode.ENTERED_THE_WRONG_PASSWORD;
import static com.example.a_uction.exception.constants.ErrorCode.USER_NOT_FOUND;
import static com.example.a_uction.exception.constants.ErrorCode.USER_PROFILE_IMAGE_IS_EMPTY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.user.dto.InfoUser;
import com.example.a_uction.model.user.dto.ModifyUser;
import com.example.a_uction.security.jwt.JwtProvider;
import com.example.a_uction.service.user.UserInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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

	private static final String TEST_IMAGE_SRC = "src/test/resources/image/test.png";

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
			.description("나는야 경매왕")
			.build();

		ModifyUser.Response response = ModifyUser.Response.builder()
			.username("test1")
			.phoneNumber("01043214321")
			.description("나는야 경매왕")
			.build();

		given(userInfoService.modifyUserDetail(any(), any())).willReturn(response);

		//when
		//then
		mockMvc.perform(put("/users/detail/modify").with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateUser)))
			.andExpect(jsonPath("$.username").value("test1"))
			.andExpect(jsonPath("$.phoneNumber").value("01043214321"))
			.andExpect(jsonPath("$.description").value("나는야 경매왕"))
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
			.description("나는야 경매왕")
			.build();

		given(userInfoService.modifyUserDetail(any(), any())).willThrow(
			new AuctionException(USER_NOT_FOUND));

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
			.description("나는야 경매왕")
			.build();

		given(userInfoService.modifyUserDetail(any(), any())).willThrow(
			new AuctionException(ENTERED_THE_WRONG_PASSWORD));

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
			.description("나는야 경매왕")
			.build();

		given(userInfoService.userInfo(any())).willReturn(infoUser);
		//when
		//then
		mockMvc.perform(get("/users/detail").with(csrf()))
			.andExpect(jsonPath("$.username").value("test"))
			.andExpect(jsonPath("$.userEmail").value("test@test.com"))
			.andExpect(jsonPath("$.phoneNumber").value("01012345678"))
			.andExpect(jsonPath("$.description").value("나는야 경매왕"))
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

	@Test
	@WithMockUser
	@DisplayName("프로필이미지추가")
	void user_modifyProfileImage() throws Exception {
		//given
		given(userInfoService.modifyProfileImage(any(), any()))
			.willReturn(InfoUser.builder()
			.username("test")
			.profileImageSrc("~/test/docker.png")
			.userEmail("test@test.com")
			.phoneNumber("01012345678")
			.description("나는야 경매왕")
			.build());
		//when
		MockMultipartFile image = this.getMockMultipartFile("file", "image/png", TEST_IMAGE_SRC);

		//then
		mockMvc.perform(multipart("/users/detail/modify/profile-image")
				.file(image)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.with(csrf()))
			.andDo(print())
			.andExpect(jsonPath("$.username").value("test"))
			.andExpect(jsonPath("$.profileImageSrc").value("~/test/docker.png"))
			.andExpect(jsonPath("$.userEmail").value("test@test.com"))
			.andExpect(jsonPath("$.phoneNumber").value("01012345678"))
			.andExpect(jsonPath("$.description").value("나는야 경매왕"))
			.andExpect(status().isOk());
	}
	@Test
	@WithMockUser
	@DisplayName("프로필이미지삭제")
	void user_deleteProfileImage() throws Exception {
		//given
		InfoUser infoUser = InfoUser.builder()
			.username("test")
			.profileImageSrc(null)
			.userEmail("test@test.com")
			.phoneNumber("01012345678")
			.description("나는야 경매왕")
			.build();

		given(userInfoService.deleteProfileImage(any()))
			.willReturn(infoUser);
		//when

		//then
		mockMvc.perform(put("/users/detail/delete/profile-image")
				.with(csrf()))
			.andDo(print())
			.andExpect(jsonPath("$.username").value("test"))
			.andExpect(jsonPath("$.userEmail").value("test@test.com"))
			.andExpect(jsonPath("$.phoneNumber").value("01012345678"))
			.andExpect(jsonPath("$.description").value("나는야 경매왕"))
			.andExpect(jsonPath("$.profileImageSrc").isEmpty())
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	@DisplayName("프로필사진 삭제 실패")
	void deleteUserProfileImage_fail() throws Exception {
		//given
		given(userInfoService.deleteProfileImage(any()))
			.willThrow(new AuctionException(USER_PROFILE_IMAGE_IS_EMPTY));
		//when
		//then
		mockMvc.perform(put("/users/detail/delete/profile-image")
				.with(csrf()))
			.andExpect(jsonPath("$.errorCode")
				.value("USER_PROFILE_IMAGE_IS_EMPTY"))
			.andExpect(status().isOk());
	}
	private MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path)
		throws IOException {
		FileInputStream fileInputStream = new FileInputStream(path);
		return new MockMultipartFile(fileName, fileName + "." + contentType, contentType,
			fileInputStream);
	}
}