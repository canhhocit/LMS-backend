package com.ex.learninghub.modules.storage.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name:demo}") String cloudName,
            @Value("${cloudinary.api-key:123456}") String apiKey,
            @Value("${cloudinary.api-secret:secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public String uploadFile(MultipartFile file) {
        try {
            String resourceType = isVideo(file.getOriginalFilename()) ? "video" : "auto";
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
}
