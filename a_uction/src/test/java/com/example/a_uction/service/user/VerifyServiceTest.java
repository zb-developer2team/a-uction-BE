package com.example.a_uction.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.user.dto.Verify;
import com.example.a_uction.model.user.dto.Verify.Form;
import com.example.a_uction.model.user.entity.UserVerificationEntity;
import com.example.a_uction.model.user.repository.UserVerificationEntityRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VerifyServiceTest{
	@Mock
	private UserVerificationEntityRepository verificationEntityRepository;
	@InjectMocks
	private VerifyService verifyService;

	@Test
	@DisplayName("verifyService - 코드체크 성공")
	void codeCheck_SUCCESS() {
	    //given
		given(verificationEntityRepository
			.findByCodeAndPhoneNumber(anyString(),anyString()))
			.willReturn(Optional.of(UserVerificationEntity.builder()
					.phoneNumber("01012345678")
					.code("1234")
				.build()));
	    //when
		boolean codeCheck = verifyService.verifyCode(
			new Form("01012345678", "1234"));
	    //then

		assertTrue(codeCheck);
	}
	@Test
	@DisplayName("verifyService - 코드체크 실패")
	void codeCheck_FAIL() {
		//given
		given(verificationEntityRepository
			.findByCodeAndPhoneNumber(anyString(),anyString()))
			.willReturn(Optional.empty());
		//when
		AuctionException auctionException = assertThrows(AuctionException.class,
			() -> verifyService.verifyCode(
				new Verify.Form("01012345678", "qwe123")));
		//then

		assertEquals(ErrorCode.WRONG_CODE_INPUT, auctionException.getErrorCode());
	}
}