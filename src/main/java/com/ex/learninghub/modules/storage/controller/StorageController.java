package com.ex.learninghub.modules.storage.controller;

import com.ex.learninghub.modules.storage.service.FileStorageRouterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

    private final FileStorageRouterService fileStorageRouterService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileStorageRouterService.uploadFile(file);
        return ResponseEntity.ok(Map.of("url", fileUrl));
    }
}
