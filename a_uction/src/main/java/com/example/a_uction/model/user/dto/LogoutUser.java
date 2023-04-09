package com.example.a_uction.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutUser {
	private String userEmail;

	// 아직 request, response 데이터 확정하지 못함
}
