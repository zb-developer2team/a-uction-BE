package com.example.a_uction.service.user;

import static com.example.a_uction.exception.constants.ErrorCode.ENTERED_THE_WRONG_PASSWORD;
import static com.example.a_uction.exception.constants.ErrorCode.USER_NOT_FOUND;
import static com.example.a_uction.exception.constants.ErrorCode.USER_PROFILE_IMAGE_IS_EMPTY;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.user.dto.InfoUser;
import com.example.a_uction.model.user.dto.ModifyUser;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import com.example.a_uction.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final S3Service s3Service;

	public ModifyUser.Response modifyUserDetail(String userEmail, ModifyUser.Request updateRequest) {
		UserEntity userEntity = userRepository.findByUserEmail(userEmail)
				.orElseThrow(() -> new AuctionException(USER_NOT_FOUND));

		//현재 비밀번호 일치 여부확인
		if(!passwordEncoder.matches(updateRequest.getCurrentPassword(), userEntity.getPassword())){
			throw new AuctionException(ENTERED_THE_WRONG_PASSWORD);
		}

		if(!updateRequest.getUpdatePassword().isEmpty()){
			userEntity.setPassword(passwordEncoder.encode(updateRequest.getUpdatePassword()));
		}

		userEntity.updateUserEntity(updateRequest);

		return ModifyUser.Response.fromEntity(userRepository.save(userEntity));
	}

	public InfoUser userInfo(String userEmail){
		UserEntity userEntity = userRepository.findByUserEmail(userEmail)
				.orElseThrow(() -> new AuctionException(USER_NOT_FOUND));
		return InfoUser.fromEntity(userEntity);
	}

	public InfoUser modifyProfileImage(MultipartFile file, String userEmail) {

		UserEntity user = userRepository.getByUserEmail(userEmail);
		String profileImageSrc = user.getProfileImageSrc();

		if (profileImageSrc != null){
			profileImageSrc = s3Service.getFileNameByUrl(profileImageSrc);
			s3Service.delete(profileImageSrc);
		}

		user.setProfileImageSrc(s3Service.upload(file));

		return InfoUser.fromEntity(userRepository.save(user));
	}

	public InfoUser deleteProfileImage(String userEmail) {
		UserEntity user = userRepository.getByUserEmail(userEmail);
		String profileImageSrc = user.getProfileImageSrc();

		if (profileImageSrc == null){
			throw new AuctionException(USER_PROFILE_IMAGE_IS_EMPTY);
		}

		profileImageSrc = s3Service.getFileNameByUrl(profileImageSrc);
		s3Service.delete(profileImageSrc);
		user.setProfileImageSrc(null);

		return InfoUser.fromEntity(userRepository.save(user));
	}
}
