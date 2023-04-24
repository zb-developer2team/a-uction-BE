package com.example.a_uction.service.auction;

import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import com.example.a_uction.service.s3.S3Service;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AuctionFileService {

	private final S3Service s3Service;
	private final AuctionRepository auctionRepository;

	public AuctionEntity addFiles(List<MultipartFile> files, AuctionEntity auction) {
		if (files != null) {
			auction.setImagesSrc(s3Service.uploadFiles(files));
			return auctionRepository.save(auction);
		}
		return auction;
	}

	@Transactional
	public AuctionEntity addImage(MultipartFile file, AuctionEntity auction) {
		// 원래 매핑되어있던 파일 db, s3 삭제
		auction.getImagesSrc().add(s3Service.upload(file));

		return auctionRepository.save(auction);
	}

	@Transactional
	public AuctionEntity deleteImage(String fileUrl, AuctionEntity auction) {
		// 원래 매핑되어있던 파일 db, s3 삭제
		s3Service.delete(s3Service.getFileNameByUrl(fileUrl));

		auction.getImagesSrc().remove(fileUrl);

		return auctionRepository.save(auction);
	}

	public void deleteS3Files(Long auctionId) {
		s3Service.deleteFiles(this.getFileNamesByAuctionId(auctionId));
	}

	private List<String> getFileNamesByAuctionId(Long auctionId) {
		return auctionRepository.getByAuctionId(auctionId)
			.getImagesSrc();
	}


}
