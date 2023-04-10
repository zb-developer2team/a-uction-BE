package com.example.a_uction.service;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.user.dto.InfoUser;
import com.example.a_uction.model.user.dto.ModifyUser;
import com.example.a_uction.model.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.a_uction.exception.constants.ErrorCode.ENTERED_THE_WRONG_PASSWORD;
import static com.example.a_uction.exception.constants.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public ModifyUser.Response modifyUserDetail(String userEmail, ModifyUser.Request updateRequest) {
		var userEntity = userRepository.findByUserEmail(userEmail)
				.orElseThrow(() -> new AuctionException(USER_NOT_FOUND));

		//현재 비밀번호 일치 여부확인
		if(!passwordEncoder.matches(updateRequest.getCurrentPassword(), userEntity.getPassword())){
			throw new AuctionException(ENTERED_THE_WRONG_PASSWORD);
		}

		userEntity.setUsername(updateRequest.getUsername());
		userEntity.setPhoneNumber(updateRequest.getPhone());

		if(!updateRequest.getUpdatePassword().isEmpty()){
			userEntity.setPassword(passwordEncoder.encode(updateRequest.getUpdatePassword()));
		}

		return new ModifyUser.Response().fromEntity(userRepository.save(userEntity));
	}

	public InfoUser userInfo(String userEmail){
		var userEntity = userRepository.findByUserEmail(userEmail)
				.orElseThrow(() -> new AuctionException(USER_NOT_FOUND));
		return new InfoUser().fromEntity(userEntity);
	}
}
