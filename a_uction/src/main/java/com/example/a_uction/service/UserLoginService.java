package com.example.a_uction.service;

import static com.example.a_uction.exception.constants.ErrorCode.ENTERED_THE_WRONG_PASSWORD;
import static com.example.a_uction.exception.constants.ErrorCode.USER_NOT_FOUND;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.user.dto.LoginUser;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import com.example.a_uction.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLoginService {
	private final UserRepository userRepository;
	private final JwtProvider provider;
	private final BCryptPasswordEncoder encoder;
	public String login(LoginUser loginUser) {

		UserEntity user = userRepository.findByUserEmail(loginUser.getUserEmail())
			.orElseThrow(
				() -> new AuctionException(USER_NOT_FOUND)
			);

		if (!validationLogin(loginUser.getPassword(), user.getPassword())) {
			throw new AuctionException(ENTERED_THE_WRONG_PASSWORD);
		}

		return provider.createToken(user.getUserEmail());
	}

	private boolean validationLogin(String formPassword, String encodingPassword) {
		return encoder.matches(formPassword, encodingPassword);
	}
}
