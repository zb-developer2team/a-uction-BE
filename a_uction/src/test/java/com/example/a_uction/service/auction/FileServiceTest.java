package com.example.a_uction.service.auction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.example.a_uction.model.auction.constants.ItemStatus;
import com.example.a_uction.model.auction.entity.AuctionEntity;
import com.example.a_uction.model.file.entity.FileEntity;
import com.example.a_uction.model.file.repository.FileRepository;
import com.example.a_uction.service.s3.S3Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private S3Service s3Service;
    @Mock
    private FileRepository fileRepository;
    @InjectMocks
    private FileService fileService;

    @Test
    @DisplayName("파일 등록 성공")
    void add_SUCCESS() throws IOException {

        //given
        List<FileEntity> files = new ArrayList<>();
        files.add(FileEntity.builder()
            .fileName("test")
            .src("test.png")
            .build());

        AuctionEntity auction = AuctionEntity.builder()
            .itemName("test item")
            .build();

        MultipartFile file = this.getMockMultipartFile("test", "image/png",
            "src/test/resources/image/test.png");
        List<MultipartFile> mockFiles = Arrays.asList(file);

        given(s3Service.uploadFiles(any()))
            .willReturn(files);

        //when
        AuctionEntity result = fileService.addFiles(mockFiles, auction);

        //then
        assertEquals(result.getFiles().size(), 1);
        assertEquals(result.getFiles().get(0).getFileName(), "test");
        assertEquals(result.getFiles().get(0).getSrc(), "test.png");
    }

    @Test
    @DisplayName("파일 수정 성공")
    void update_SUCCESS() throws IOException {
        //given
        AuctionEntity auction = AuctionEntity.builder()
            .auctionId(1L)
            .build();

        List<MultipartFile> files = this.getFiles();

        List<FileEntity> fileEntities = List.of(FileEntity.builder()
            .src("test.png")
            .fileName("test")
            .build());

        given(s3Service.uploadFiles(files))
            .willReturn(fileEntities);

        //when
        AuctionEntity result = fileService.updateFiles(files, auction);

        //then
        assertEquals("test", result.getFiles().get(0).getFileName());
        assertEquals("test.png", result.getFiles().get(0).getSrc());
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
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        return new MockMultipartFile(fileName, fileName + "." + contentType, contentType,
            fileInputStream);
    }
}