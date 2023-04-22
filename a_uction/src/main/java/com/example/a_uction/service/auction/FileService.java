package com.example.a_uction.service.auction;

import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.file.entity.FileEntity;
import com.example.a_uction.model.file.repository.FileRepository;
import com.example.a_uction.service.s3.S3Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {

	private final FileRepository fileRepository;
	private final S3Service s3Service;

	// 추가
	// 파일 업로드 및 저장 -> 옥션 엔티티 저장 -> 응답 dto 반환
	public AuctionEntity addFiles(List<MultipartFile> files, AuctionEntity auction) {
		if (files != null) {
			List<FileEntity> fileEntities = s3Service.uploadFiles(files);
			fileEntities.stream().forEach(fileEntity -> auction.addFile(fileEntity));
		}
		return auction;
	}

	@Transactional
	public AuctionEntity updateFiles(List<MultipartFile> files, AuctionEntity auction) {
		// 원래 매핑되어있던 파일 db, s3 삭제
		List<String> fileNames = this.getFileNamesByAuctionId(auction.getAuctionId());
		fileNames.stream().forEach(fileName -> s3Service.delete(fileName));

		fileRepository.deleteAllByAuction_AuctionId(auction.getAuctionId());

		// 파일엔터티 추가, 저장
		return this.addFiles(files, auction);
	}

	// 추가
	public void deleteS3Files(Long auctionId) {
		s3Service.deleteFiles(this.getFileNamesByAuctionId(auctionId));
	}

	public List<String> getFileNamesByAuctionId(Long auctionId) {
		return fileRepository.findAllByAuction_AuctionId(auctionId)
			.orElse(Collections.emptyList())
			.stream().map(fileEntity -> fileEntity.getFileName()).collect(Collectors.toList());

	}


}
