package com.example.a_uction.service.auction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import com.example.a_uction.service.s3.S3Service;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class AuctionFileServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private AuctionRepository auctionRepository;

	@InjectMocks
    private AuctionFileService auctionFileService;

    @Test
    @DisplayName("파일리스트 등록 성공")
    void add_SUCCESS() throws IOException {

        //given
		List<String> files = List.of("~/test/image.png", "~/test/docker.png");

		AuctionEntity auction = AuctionEntity.builder()
            .itemName("test item")
            .filesSrc(new ArrayList<>())
            .build();

        List<MultipartFile> mockFiles = getFiles();

        given(s3Service.uploadFiles(any()))
            .willReturn(files);

        auction.setFilesSrc(files);
        given(auctionRepository.save(any()))
            .willReturn(auction);

        //when
        AuctionEntity result = auctionFileService.addFiles(mockFiles, auction);

        //then
        assertEquals(result.getFilesSrc().size(), 2);
        assertEquals(result.getFilesSrc().get(0), "~/test/image.png");
        assertEquals(result.getFilesSrc().get(1), "~/test/docker.png");
    }

    @Test
    @DisplayName("이미지 추가")
    void addImage_success () throws IOException {
        //given
        AuctionEntity auction = AuctionEntity.builder()
            .itemName("test item")
            .filesSrc(new ArrayList<>())
            .build();

        MultipartFile file = this.getMockMultipartFile("test", "image/png",
            "src/test/resources/image/test.png");

        auction.getFilesSrc().add("~/test/image.png");

        given(s3Service.upload(any()))
            .willReturn("~/test/docker.png");

        given(auctionRepository.save(any()))
            .willReturn(auction);

        //when
        AuctionEntity auctionEntity = auctionFileService.addImage(file, auction);

        //then
        assertEquals(auctionEntity.getFilesSrc().size(), 2);
        assertEquals(auctionEntity.getFilesSrc().get(0), "~/test/image.png");
        assertEquals(auctionEntity.getFilesSrc().get(1), "~/test/docker.png");
    }

    @Test
    void deleteImage_success() {
        //given
        AuctionEntity auction = AuctionEntity.builder()
            .itemName("test item")
            .filesSrc(new ArrayList<>())
            .build();
        auction.getFilesSrc().add("~/test/docker.png");
        auction.getFilesSrc().add("~/test/image.png");
        String fileUrl = "~/test/docker.png";

        given(auctionRepository.save(any()))
            .willReturn(AuctionEntity.builder()
                .itemName("test item")
                .filesSrc(
                    List.of("~/test/docker.png", "~/test/image.png"))
                .build()
            );
        //when
        AuctionEntity auctionEntity = auctionFileService.deleteImage(fileUrl, auction);

        //then
        ArgumentCaptor<AuctionEntity> captor = ArgumentCaptor.forClass(AuctionEntity.class);
        verify(auctionRepository, times(1)).save(captor.capture());

        assertEquals(captor.getValue().getFilesSrc().size(), 1);
        assertEquals(captor.getValue().getFilesSrc().get(0), "~/test/image.png");
        assertEquals(auctionEntity.getFilesSrc().size(), 2);
        assertEquals(auctionEntity.getFilesSrc().get(0), "~/test/docker.png");
        assertEquals(auctionEntity.getFilesSrc().get(1), "~/test/image.png");
    }

    private List<MultipartFile> getFiles() throws IOException {
        List<MultipartFile> files = new ArrayList<>();
        MultipartFile file = getMockMultipartFile("test", "image/png",
            "src/test/resources/image/test.png");

        files.add(file);
        files.add(getMockMultipartFile("test2", "image/png",
            "src/test/resources/image/test.png"));
        return files;
    }

    private MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path)
        throws IOException {
        FileInputStream fileInputStream = new FileInputStream(path);
        return new MockMultipartFile(fileName, fileName + "." + contentType, contentType,
            fileInputStream);
    }
}