package com.example.a_uction.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.user.dto.RegisterUser;
import com.example.a_uction.model.user.dto.RegisterUser.Request;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserRegisterServiceTest {

	@Mock
	private UserRepository userRepository;
	@Spy
	private BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	private UserRegisterService userRegisterService;

	@Test
	@DisplayName("회원가입성공")
	void register_SUCCESS() {
		//given
		given(userRepository.findByUserEmail(any()))
			.willReturn(Optional.empty());
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

	@Test
	@DisplayName("이메일중복체크 - 사용 가능")
	void emailCheck_SUCCESS() {
	    //given
		given(userRepository.findByUserEmail(any()))
			.willReturn(Optional.empty());
	    //when
		boolean emailCheck = userRegisterService.emailCheck("zerobase@gmail.com");
	    //then
		assertTrue(emailCheck);
	}
	@Test
	@DisplayName("이메일중복체크 - 사용 불가")
	void emailCheck_FAIL() {
		//given
		given(userRepository.findByUserEmail(any()))
			.willReturn(Optional.ofNullable(UserEntity.builder()
				.userEmail("zerobase@gmail.com")
				.username("zerobase")
				.password("1234")
				.phoneNumber("01012345678")
				.build()));
		//when
		AuctionException auctionException = assertThrows(AuctionException.class,
			() -> userRegisterService.emailCheck("zerobase@gmail.com"));
	    //then
		assertEquals(ErrorCode.THIS_EMAIL_ALREADY_EXIST, auctionException.getErrorCode());
	}
}