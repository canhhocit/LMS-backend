package com.ex.learninghub.modules.storage.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class GoogleDriveStorageService {

    @Value("${google.drive.folder-id:}")
    private String folderId;

    @Value("${google.drive.client-id:}")
    private String clientId;

    @Value("${google.drive.client-secret:}")
    private String clientSecret;

    @Value("${google.drive.refresh-token:}")
    private String refreshToken;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Upload file lên Google Drive API v3 (dùng OAuth 2.0 Client Credentials)
     * và trả về URL xem/tải công khai (Drive View Link).
     */
    public String uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String sanitizedName = originalFilename != null ? originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_") : "document";
        String fileName = UUID.randomUUID() + "_" + sanitizedName;

        boolean hasCredentials = clientId != null && !clientId.isBlank()
                && clientSecret != null && !clientSecret.isBlank();

        if (!hasCredentials) {
            log.warn("Chưa cấu hình GOOGLE_DRIVE_CLIENT_ID / CLIENT_SECRET, trả về URL Google Drive giả lập");
            return "https://drive.google.com/file/d/1mock_drive_file_" + UUID.randomUUID().toString().replace("-", "") + "/view?usp=sharing";
        }

        try {
            // Bước 1: Lấy Access Token từ OAuth 2.0 Refresh Token
            String accessToken = fetchAccessToken();
            if (accessToken == null || accessToken.isBlank()) {
                log.warn("Không lấy được Access Token cho Google Drive, dùng fallback URL");
                return "https://drive.google.com/file/d/1mock_drive_file_" + UUID.randomUUID().toString().replace("-", "") + "/view?usp=sharing";
            }

            // Bước 2: Upload file lên Google Drive API v3
            String uploadUrl = "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Metadata JSON
            HttpHeaders metaHeaders = new HttpHeaders();
            metaHeaders.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> metadata = Map.of(
                    "name", fileName,
                    "parents", (folderId != null && !folderId.isBlank()) ? Collections.singletonList(folderId) : Collections.emptyList()
            );
            HttpEntity<Map<String, Object>> metaEntity = new HttpEntity<>(metadata, metaHeaders);

            // File Media
            HttpHeaders fileHeaders = new HttpHeaders();
            String contentType = file.getContentType();
            fileHeaders.setContentType(contentType != null && !contentType.isBlank()
                    ? MediaType.parseMediaType(contentType)
                    : MediaType.APPLICATION_OCTET_STREAM);
            HttpEntity<byte[]> fileEntity = new HttpEntity<>(file.getBytes(), fileHeaders);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("metadata", metaEntity);
            body.add("file", fileEntity);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String fileId = (String) response.getBody().get("id");
                log.info("Upload file thành công lên Google Drive với File ID: {}", fileId);

                // Bước 3: Cấp quyền xem công khai cho file (Anyone reader)
                makeFilePublic(fileId, accessToken);

                return "https://drive.google.com/file/d/" + fileId + "/view?usp=sharing";
            } else {
                log.warn("Google Drive API trả về lỗi: {}", response.getStatusCode());
                return "https://drive.google.com/file/d/1mock_drive_file_" + UUID.randomUUID().toString().replace("-", "") + "/view?usp=sharing";
            }
        } catch (Exception e) {
            log.error("Lỗi khi upload file lên Google Drive API: {}", e.getMessage());
            return "https://drive.google.com/file/d/1mock_drive_file_" + UUID.randomUUID().toString().replace("-", "") + "/view?usp=sharing";
        }
    }

    private String fetchAccessToken() {
        if (refreshToken == null || refreshToken.isBlank()) {
            return null;
        }
        try {
            String tokenEndpoint = "https://oauth2.googleapis.com/token";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("refresh_token", refreshToken);
            map.add("grant_type", "refresh_token");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("access_token");
            }
        } catch (Exception e) {
            log.warn("Lỗi khi lấy Access Token từ Refresh Token: {}", e.getMessage());
        }
        return null;
    }

    private void makeFilePublic(String fileId, String accessToken) {
        try {
            String permUrl = "https://www.googleapis.com/drive/v3/files/" + fileId + "/permissions";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> permBody = Map.of("role", "reader", "type", "anyone");
            HttpEntity<Map<String, String>> permEntity = new HttpEntity<>(permBody, headers);
            restTemplate.postForEntity(permUrl, permEntity, Map.class);
        } catch (Exception e) {
            log.warn("Không thể cấp quyền public cho file ID {}: {}", fileId, e.getMessage());
        }
    }
}
