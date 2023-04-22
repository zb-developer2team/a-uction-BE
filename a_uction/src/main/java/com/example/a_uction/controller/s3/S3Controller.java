package com.example.a_uction.controller.s3;

import com.example.a_uction.service.s3.S3Service;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

	private final S3Service s3Service;

	// 테스트
	@PostMapping("/upload")
	public ResponseEntity<?> upload(@RequestParam("images") List<MultipartFile> files) {
		return ResponseEntity.ok(s3Service.uploadFiles(files));
	}


}
