package com.example.a_uction.model.user.dto;

import com.example.a_uction.model.user.entity.BalanceHistoryEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class Balance {

	@Getter
	@Setter
	@Builder
	public static class Request {

		private String from;
		private Integer money;

	}

	@Getter
	@Builder
	public static class Response {

		private String from;
		private Integer changeMoney;
		private Integer currentMoney;
		private String userEmail;

		public static Response fromEntity(BalanceHistoryEntity entity) {
			return Response.builder()
				.from(entity.getFromMessage())
				.changeMoney(entity.getChangeMoney())
				.currentMoney(entity.getCurrentMoney())
				.userEmail(entity.getUser().getUserEmail())
				.build();
		}

	}

}
