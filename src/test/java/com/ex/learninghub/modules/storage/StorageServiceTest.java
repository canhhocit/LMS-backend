package com.ex.learninghub.modules.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.ex.learninghub.modules.storage.service.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        when(cloudinary.uploader()).thenReturn(uploader);
        cloudinaryService = new CloudinaryService(cloudinary);
    }

    @Test
    void uploadVideo_returnsSecureUrlAndDurationInSeconds() throws Exception {
        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(Map.of("secure_url", "https://cdn.example/video.mp4", "duration", 12.4d));

        var result = cloudinaryService.uploadVideo(new MockMultipartFile(
                "file", "lesson.mp4", "video/mp4", new byte[]{1, 2, 3}));

        assertThat(result.secureUrl()).isEqualTo("https://cdn.example/video.mp4");
        assertThat(result.durationSeconds()).isEqualTo(13);
    }

    @Test
    void uploadFile_propagatesCloudinaryFailureInsteadOfReturningMockUrl() throws Exception {
        when(uploader.upload(any(byte[].class), anyMap())).thenThrow(new RuntimeException("Cloudinary unavailable"));

        assertThatThrownBy(() -> cloudinaryService.uploadFile(new MockMultipartFile(
                "file", "lesson.mp4", "video/mp4", new byte[]{1, 2, 3})))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cloudinary upload failed");
    }

    @Test
    void uploadVideo_rejectsMissingDurationMetadata() throws Exception {
        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(Map.of("secure_url", "https://cdn.example/video.mp4"));

        assertThatThrownBy(() -> cloudinaryService.uploadVideo(new MockMultipartFile(
                "file", "lesson.mp4", "video/mp4", new byte[]{1, 2, 3})))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cloudinary did not return a valid video duration");
    }
}
