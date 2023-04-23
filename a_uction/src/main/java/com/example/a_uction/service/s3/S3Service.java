package com.example.a_uction.service.s3;

import static com.example.a_uction.exception.constants.ErrorCode.FAILED_FILE_UPLOAD;
import static com.example.a_uction.exception.constants.ErrorCode.INVALID_FILE_FORM;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.a_uction.exception.AuctionException;
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

	public List<String> uploadFiles(List<MultipartFile> files) {

		return files.stream()
				.map(this::upload)
			.collect(Collectors.toList());
	}

	public String upload(MultipartFile file) {

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

		return amazonS3.getUrl(bucket, s3FileName).toString();
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
		fileNames.forEach(this::delete);
	}

	public void delete(String fileName) {
		amazonS3.deleteObject(bucket, fileName);
	}

	public String getFileNameByUrl(String fileUrl) {
		String[] str = fileUrl.split("/");
		return str[str.length - 1];
	}
}
