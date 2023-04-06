package com.example.a_uction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.a_uction.service.UserRegisterService;
import com.example.a_uction.model.user.dto.RegisterUser;
import com.example.a_uction.model.user.dto.RegisterUser.Request;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserRegisterServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	private UserRegisterService userRegisterService;

	@Test
	@DisplayName("회원가입성공")
	void register_SUCCESS() {
		//given
		given(userRepository.save(any()))
			.willReturn(UserEntity.builder()
				.userEmail("zerobase@gmail.com")
				.username("zerobase")
				.password("1234")
				.phoneNumber("01012345678")
				.build());
		//when
		RegisterUser registerUser =
			userRegisterService.register(
				new Request("ss@ss",
					"1111",
					"ss",
					"01011111111")
			);
		ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
		//then
		verify(userRepository, times(1)).save(captor.capture());
		assertEquals("ss@ss", captor.getValue().getUserEmail());
		assertEquals("zerobase@gmail.com", registerUser.getUserEmail());
		assertEquals("zerobase", registerUser.getUsername());
	}
	//TODO CustomException 생성 후 회원가입 실패 테스트 작성
}