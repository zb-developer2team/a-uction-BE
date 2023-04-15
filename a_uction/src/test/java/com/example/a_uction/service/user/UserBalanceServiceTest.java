package com.example.a_uction.service.user;

import static com.example.a_uction.exception.constants.ErrorCode.NOT_ENOUGH_BALANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.user.dto.Balance;
import com.example.a_uction.model.user.entity.BalanceHistoryEntity;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.BalanceHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserBalanceServiceTest {


	@Mock
	private BalanceHistoryRepository balanceHistoryRepository;
	@InjectMocks
	private UserBalanceService userBalanceService;

	@DisplayName("예치금 변경 성공")
	@Test
	void change_SUCCESS() {
		// given
		Balance.Request request = Balance.Request.builder()
			.from("충전")
			.money(100000)
			.build();

		BalanceHistoryEntity balanceHistory = BalanceHistoryEntity.builder()
			.id(1L)
			.currentMoney(0)
			.changeMoney(0)
			.user(UserEntity.builder()
				.balance(0)
				.userEmail("zerobase@gmail.com")
				.password("1234")
				.build())
			.build();

		given(balanceHistoryRepository.save(any()))
			.willReturn(BalanceHistoryEntity.builder()
				.changeMoney(100)
				.currentMoney(200)
				.user(UserEntity.builder()
					.userEmail("zerobase@gmail.com")
					.balance(0)
					.build())
				.build());

		// when
		Balance.Response response = userBalanceService.change(balanceHistory, request);

		// then
		ArgumentCaptor<BalanceHistoryEntity> captor = ArgumentCaptor.forClass(
			BalanceHistoryEntity.class);
		verify(balanceHistoryRepository, times(1)).save(captor.capture());
		assertEquals(100000, captor.getValue().getChangeMoney());
		assertEquals(100000, captor.getValue().getCurrentMoney());
		assertEquals("충전", captor.getValue().getFromMessage());
		assertEquals("zerobase@gmail.com", captor.getValue().getUser().getUserEmail());

		assertEquals(100, response.getChangeMoney());
		assertEquals(200, response.getCurrentMoney());
		assertEquals("zerobase@gmail.com", response.getUserEmail());

	}

	@DisplayName("예치금 변경 실패")
	@Test
	void change_FAIL() {
		// given
		Balance.Request request = Balance.Request.builder()
			.from("충전")
			.money(-5000)
			.build();

		BalanceHistoryEntity balanceHistory = BalanceHistoryEntity.builder()
			.id(1L)
			.currentMoney(0)
			.changeMoney(0)
			.user(UserEntity.builder()
				.balance(0)
				.userEmail("zerobase@gmail.com")
				.password("1234")
				.build())
			.build();

		// when
		AuctionException exception = assertThrows(AuctionException.class,
			() -> userBalanceService.change(balanceHistory, request));

		// then
		assertEquals(NOT_ENOUGH_BALANCE, exception.getErrorCode());
	}

}