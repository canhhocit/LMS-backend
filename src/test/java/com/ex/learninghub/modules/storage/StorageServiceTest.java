package com.ex.learninghub.modules.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ex.learninghub.modules.storage.service.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

class StorageServiceTest {

    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "demo",
                "api_key", "123456",
                "api_secret", "secret"
        ));
        cloudinaryService = new CloudinaryService(cloudinary);
    }

    @Test
    @DisplayName("Nên upload file và trả về URL Cloudinary thành công")
    void uploadFile_Success() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-avatar.png",
                "image/png",
                "dummy image content".getBytes()
        );

        String url = cloudinaryService.uploadFile(file);

        assertThat(url).isNotNull();
        assertThat(url).startsWith("http");
    }
}
