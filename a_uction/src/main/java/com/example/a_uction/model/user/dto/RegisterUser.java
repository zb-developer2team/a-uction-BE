package com.example.a_uction.model.user.dto;

import com.example.a_uction.model.user.entity.UserEntity;
import javax.validation.constraints.NotNull;
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
public class RegisterUser {
	private String userEmail;
	private String username;

	public static RegisterUser fromEntity(UserEntity userEntity) {

		return RegisterUser.builder()
			.userEmail(userEntity.getUserEmail())
			.username(userEntity.getUsername())
			.build();
	}
	@Getter
	@Builder //테스트용
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {
		@NotNull
		private String userEmail;
		@NotNull
		private String password;
		@NotNull
		private String username;
		@NotNull
		private String phoneNumber;
	}
}
