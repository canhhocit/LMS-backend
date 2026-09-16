package com.ex.learninghub.modules.admin;

import com.ex.learninghub.modules.admin.service.DatabaseBackupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseBackupTest {

    private DatabaseBackupService backupService;

    @BeforeEach
    void setUp() {
        backupService = new DatabaseBackupService();
    }

    @Test
    @DisplayName("Nên tạo bản sao lưu CSDL thành công")
    void triggerBackup_Success() {
        DatabaseBackupService.BackupStatus status = backupService.triggerBackup();

        assertThat(status).isNotNull();
        assertThat(status.getStatus()).isEqualTo("COMPLETED");
        assertThat(status.getFilename()).endsWith(".sql.gz");
    }
}
