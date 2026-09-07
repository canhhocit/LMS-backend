package com.ex.learninghub.modules.audit.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponse {
    private Long id;
    private Long actorId;
    private String actorEmail;
    private String action;
    private String resourceType;
    private String resourceId;
    private String detail;
    private String ipAddress;
    private String result;
    private LocalDateTime createdAt;
}
