package com.example.a_uction.model.user.service;

import com.example.a_uction.model.user.dto.RegisterUser;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegisterService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public RegisterUser register(RegisterUser.Request request) {
		if (userRepository.findByUserEmail(request.getUserEmail()).isPresent()) {
			//TODO CustomException 발생
			log.info("중복된 이메일 가입 시도");
			throw new RuntimeException("이미 가입 된 이메일");
		}
		return RegisterUser.fromEntity(
			userRepository.save(UserEntity.builder()
			.username(request.getUsername())
			.phoneNumber(request.getPhoneNumber())
			.password(passwordEncoder.encode(request.getPassword()))
			.userEmail(request.getUserEmail())
			.build()));
	}

}
