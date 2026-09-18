package com.ex.learninghub.modules.auth.repository;

import com.ex.learninghub.modules.auth.entity.PermissionAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PermissionAuditLogRepository extends JpaRepository<PermissionAuditLog, Long> {
    List<PermissionAuditLog> findAllByOrderByTimestampDesc();
}
