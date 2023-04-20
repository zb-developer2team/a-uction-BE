package com.example.a_uction.service.s3;

import static com.example.a_uction.exception.constants.ErrorCode.FAILED_FILE_UPLOAD;
import static com.example.a_uction.exception.constants.ErrorCode.INVALID_FILE_FORM;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.file.entity.FileEntity;
import com.example.a_uction.model.file.repository.FileRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3Service {
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final AmazonS3 amazonS3;
	private final FileRepository fileRepository;

	public List<FileEntity> uploadFiles(List<MultipartFile> files) {
		return files.stream().map(file -> upload(file)).collect(Collectors.toList());
	}

	private FileEntity upload(MultipartFile file) {
		String s3FileName = this.createFileName(file.getOriginalFilename());
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(file.getSize());
		objectMetadata.setContentType(file.getContentType());

		try (InputStream inputStream = file.getInputStream()) {
			amazonS3.putObject(new PutObjectRequest(bucket, s3FileName, inputStream, objectMetadata)
				.withCannedAcl(CannedAccessControlList.PublicRead));
		} catch (IOException e) {
			throw new AuctionException(FAILED_FILE_UPLOAD);
		}

		return this.saveEntity(s3FileName);
	}

	private FileEntity saveEntity(String fileName) {
		String imageUrl = amazonS3.getUrl(bucket, fileName).toString();
		return fileRepository.save(FileEntity.builder()
			.src(imageUrl)
			.fileName(fileName)
			.build());
	}

	private String createFileName(String fileName) {
		return UUID.randomUUID().toString().concat(this.getFileExtension(fileName));
	}

	private String getFileExtension(String fileName) {
		try {
			return fileName.substring(fileName.lastIndexOf("."));
		} catch (StringIndexOutOfBoundsException e) {
			throw new AuctionException(INVALID_FILE_FORM);
		}
	}

	public void deleteFiles(List<String> fileNames) {
		fileNames.stream().forEach(fileName -> this.delete(fileName));
	}

	public void delete(String fileName) {
		amazonS3.deleteObject(bucket, fileName);
	}


}
