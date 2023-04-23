package com.example.a_uction.model.user.dto;

import com.example.a_uction.model.user.entity.UserEntity;
import lombok.*;

public class ModifyUser {

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request{
		private String currentPassword;
		private String updatePassword;
		private String username;
		private String phoneNumber;
		private String description;
	}

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Response{
		private String username;
		private String phoneNumber;
		private String description;

		public static ModifyUser.Response fromEntity(UserEntity userEntity){
			return Response.builder()
					.phoneNumber(userEntity.getPhoneNumber())
					.username(userEntity.getUsername())
					.description(userEntity.getDescription())
					.build();
		}

	}

}
