package com.example.a_uction.model.user.dto;

import com.example.a_uction.model.user.entity.UserEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoUser {

	private String username;
	private String profileImageSrc;
	private String userEmail;
	private String phoneNumber;
	private String description;

	public static InfoUser fromEntity(UserEntity userEntity){
		return InfoUser.builder()
				.userEmail(userEntity.getUserEmail())
				.profileImageSrc(userEntity.getProfileImageSrc())
				.phoneNumber(userEntity.getPhoneNumber())
				.username(userEntity.getUsername())
				.description(userEntity.getDescription())
				.build();
	}

}
