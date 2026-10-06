package com.ex.learninghub.modules.storage.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
        String resourceType = isVideo(originalFilename) ? "video" : isRawDocument(originalFilename) ? "raw" : "auto";
        return secureUrl(upload(file, resourceType));
    }

    public CloudinaryUploadResult uploadVideo(MultipartFile file) {
        Map<?, ?> uploadResult = upload(file, "video");
        String secureUrl = secureUrl(uploadResult);
        Object durationValue = uploadResult.get("duration");
        if (!(durationValue instanceof Number durationNumber)
                || !Double.isFinite(durationNumber.doubleValue())
                || durationNumber.doubleValue() <= 0
                || durationNumber.doubleValue() > Integer.MAX_VALUE) {
            throw new IllegalStateException("Cloudinary did not return a valid video duration");
        }

        return new CloudinaryUploadResult(
                secureUrl,
                (int) Math.ceil(durationNumber.doubleValue())
        );
    }

    private String secureUrl(Map<?, ?> uploadResult) {
        Object secureUrl = uploadResult.get("secure_url");
        if (!(secureUrl instanceof String url) || url.isBlank()) {
            throw new IllegalStateException("Cloudinary response did not contain a secure URL");
        }
        return url;
    }

    private Map<?, ?> upload(MultipartFile file, String resourceType) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", resourceType));
            String url = secureUrl(uploadResult);
            log.info("Upload file lên Cloudinary thành công: {}", url);
            return uploadResult;
        } catch (Exception e) {
            log.error("Cloudinary upload failed", e);
            throw new IllegalStateException("Cloudinary upload failed", e);
        }
    }

    private boolean isVideo(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".mp4") || lower.endsWith(".mkv") || lower.endsWith(".avi") || lower.endsWith(".mov");
    }

    private boolean isRawDocument(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".pdf") || lower.endsWith(".doc") || lower.endsWith(".docx") 
            || lower.endsWith(".xls") || lower.endsWith(".xlsx") || lower.endsWith(".ppt") 
            || lower.endsWith(".pptx") || lower.endsWith(".txt") || lower.endsWith(".zip") 
            || lower.endsWith(".rar") || lower.endsWith(".csv");
    }
}
