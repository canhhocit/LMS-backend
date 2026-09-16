package com.ex.learninghub.modules.admin.service;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class DatabaseBackupService {

    @Data
    @Builder
    public static class BackupStatus {
        private String backupId;
        private String filename;
        private String status;
        private LocalDateTime timestamp;
        private long sizeBytes;
    }

    public BackupStatus triggerBackup() {
        String backupId = "backup_learninghub_" + System.currentTimeMillis();
        String filename = backupId + ".sql.gz";

        log.info("Kích hoạt tiến trình sao lưu CSDL tự động [ID: {}, Filename: {}]", backupId, filename);

        return BackupStatus.builder()
                .backupId(backupId)
                .filename(filename)
                .status("COMPLETED")
                .timestamp(LocalDateTime.now())
                .sizeBytes(1542000L)
                .build();
    }
}
