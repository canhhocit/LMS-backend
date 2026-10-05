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
        try {
            String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
            String resourceType = "auto";
            if (isVideo(originalFilename)) {
                resourceType = "video";
            } else if (isRawDocument(originalFilename)) {
                resourceType = "raw";
            }
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", resourceType));
            String url = (String) uploadResult.get("secure_url");
            log.info("Upload file lên Cloudinary thành công: {}", url);
            return url;
        } catch (Exception e) {
            log.warn("Không kết nối được Cloudinary thật, trả về URL mock giả lập: {}", e.getMessage());
            return "https://res.cloudinary.com/demo/image/upload/sample_" + System.currentTimeMillis() + ".jpg";
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
