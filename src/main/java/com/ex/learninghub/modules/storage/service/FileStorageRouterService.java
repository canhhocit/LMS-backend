package com.ex.learninghub.modules.storage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageRouterService {

    private final CloudinaryService cloudinaryService;
    private final GoogleDriveStorageService googleDriveStorageService;

    private static final Set<String> IMAGE_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp", "svg", "bmp", "ico", "tiff"
    );

    private static final Set<String> VIDEO_EXTENSIONS = Set.of(
            "mp4", "mkv", "avi", "mov", "webm", "flv", "wmv", "m4v", "3gp"
    );

    /**
     * Tự động điều hướng upload file:
     * - Ảnh và Video -> Lưu trên Cloudinary
     * - Các định dạng file khác (PDF, Word, Excel, ZIP, RAR, TXT...) -> Lưu trên Google Drive API (hoặc Supabase)
     */
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File upload không được để trống");
        }

        if (isImageOrVideo(file)) {
            log.info("Phát hiện file Ảnh/Video [{}], điều hướng upload tới Cloudinary", file.getOriginalFilename());
            return cloudinaryService.uploadFile(file);
        } else {
            log.info("Phát hiện file Tài liệu/Tệp tin [{}], điều hướng upload tới Google Drive API", file.getOriginalFilename());
            return googleDriveStorageService.uploadFile(file);
        }
    }

    private boolean isImageOrVideo(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null) {
            String lowerType = contentType.toLowerCase();
            if (lowerType.startsWith("image/") || lowerType.startsWith("video/")) {
                return true;
            }
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            return IMAGE_EXTENSIONS.contains(ext) || VIDEO_EXTENSIONS.contains(ext);
        }

        return false;
    }
}
