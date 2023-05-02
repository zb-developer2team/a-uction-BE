package com.example.a_uction.service.user;

import static com.example.a_uction.exception.constants.ErrorCode.THIS_EMAIL_ALREADY_EXIST;

import com.example.a_uction.exception.AuctionException;
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

		registerValidationCheck(request.getUserEmail());

		return RegisterUser.fromEntity(
			userRepository.save(UserEntity.builder()
				.username(request.getUsername())
				.phoneNumber(request.getPhoneNumber())
				.password(passwordEncoder.encode(request.getPassword()))
				.userEmail(request.getUserEmail())
				.build()));
	}

	public boolean emailCheck(String email) {
		log.info("email:{} 중복체크 시도!!", email);
		if (userRepository.findByUserEmail(email).isPresent()) {
			log.error("중복된 이메일 가입 시도");
			throw new AuctionException(THIS_EMAIL_ALREADY_EXIST);
		}
		return true;
	}

	private void registerValidationCheck(String userEmail) {
		if (userRepository.findByUserEmail(userEmail).isPresent()){
			throw new AuctionException(THIS_EMAIL_ALREADY_EXIST);
		}
	}
}
