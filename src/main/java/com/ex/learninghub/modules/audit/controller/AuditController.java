package com.ex.learninghub.modules.audit.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.audit.dto.AuditLogResponse;
import com.ex.learninghub.modules.audit.entity.AuditLog;
import com.ex.learninghub.modules.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogs(
            @RequestParam(required = false) String resourceType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<AuditLog> logsPage;
        if (resourceType != null && !resourceType.isEmpty()) {
            logsPage = auditLogRepository.findByResourceTypeOrderByCreatedAtDesc(resourceType, pageable);
        } else {
            logsPage = auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        Page<AuditLogResponse> responsePage = logsPage.map(log -> AuditLogResponse.builder()
                .id(log.getId())
                .actorId(log.getActorId())
                .actorEmail(log.getActorEmail())
                .action(log.getAction())
                .resourceType(log.getResourceType())
                .resourceId(log.getResourceId())
                .detail(log.getDetail())
                .ipAddress(log.getIpAddress())
                .result(log.getResult())
                .createdAt(log.getCreatedAt())
                .build());

        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }

    @GetMapping("/actor/{userId}")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogsByActor(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLog> logsPage = auditLogRepository.findByActorIdOrderByCreatedAtDesc(userId, pageable);

        Page<AuditLogResponse> responsePage = logsPage.map(log -> AuditLogResponse.builder()
                .id(log.getId())
                .actorId(log.getActorId())
                .actorEmail(log.getActorEmail())
                .action(log.getAction())
                .resourceType(log.getResourceType())
                .resourceId(log.getResourceId())
                .detail(log.getDetail())
                .ipAddress(log.getIpAddress())
                .result(log.getResult())
                .createdAt(log.getCreatedAt())
                .build());

        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }
}
