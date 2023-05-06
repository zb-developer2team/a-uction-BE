package com.example.a_uction.service.user;

import static com.example.a_uction.exception.constants.ErrorCode.NOT_ENOUGH_BALANCE;
import static com.example.a_uction.exception.constants.ErrorCode.USER_NOT_FOUND;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.user.dto.Balance;
import com.example.a_uction.model.user.dto.Balance.Request;
import com.example.a_uction.model.user.entity.BalanceHistoryEntity;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.BalanceHistoryRepository;
import com.example.a_uction.model.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBalanceService {

	private final BalanceHistoryRepository balanceHistoryRepository;
	private final UserRepository userRepository;

	public Balance.Response charge(String userEmail, Balance.Request chargeBalance) {

		BalanceHistoryEntity balanceHistory = this.getByUserEmail(userEmail);
		return this.change(balanceHistory, chargeBalance);

	}

	public void withdraw(String userEmail, Request withdrawBalance) {

		BalanceHistoryEntity balanceHistory = this.getByUserEmail(userEmail);
		withdrawBalance.setMoney(-withdrawBalance.getMoney());
		this.change(balanceHistory, withdrawBalance);
	}

	@Transactional
	public Balance.Response change(BalanceHistoryEntity balanceHistory,
		Balance.Request changeBalance) {


		Integer currentMoney = balanceHistory.getCurrentMoney() + changeBalance.getMoney();
		if (currentMoney < 0) {
			throw new AuctionException(NOT_ENOUGH_BALANCE);
		}

		balanceHistory.getUser().setBalance(currentMoney);
		return Balance.Response.fromEntity(
			balanceHistoryRepository.save(BalanceHistoryEntity.builder()
				.fromMessage(changeBalance.getFrom())
				.changeMoney(changeBalance.getMoney())
				.currentMoney(currentMoney)
				.user(balanceHistory.getUser())
				.build()));

	}

	private BalanceHistoryEntity getByUserEmail(String userEmail) {

		UserEntity user = userRepository.findByUserEmail(userEmail)
			.orElseThrow(() -> new AuctionException(USER_NOT_FOUND));

		return balanceHistoryRepository.findFirstByUserIdOrderByIdDesc(user.getId())
			.orElse(BalanceHistoryEntity.builder()
				.changeMoney(0)
				.currentMoney(0)
				.user(user)
				.build());


	}
}
