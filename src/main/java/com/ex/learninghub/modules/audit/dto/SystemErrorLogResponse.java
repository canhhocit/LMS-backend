package com.ex.learninghub.modules.audit.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SystemErrorLogResponse {
    private Long id;
    private Long actorId;
    private String actorEmail;
    private String exceptionType;
    private String errorMessage;
    private String requestMethod;
    private String requestPath;
    private String stackTrace;
    private LocalDateTime createdAt;
}
