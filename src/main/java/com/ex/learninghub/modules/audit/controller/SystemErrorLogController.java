package com.ex.learninghub.modules.audit.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.audit.dto.SystemErrorLogResponse;
import com.ex.learninghub.modules.audit.repository.SystemErrorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/system-error-logs")
@RequiredArgsConstructor
@PreAuthorize("hasPermission(null, 'VIEW_SYSTEM_LOGS') or hasPermission(null, 'SYSTEM_CONFIG')")
public class SystemErrorLogController {

    private final SystemErrorLogRepository repository;

    @GetMapping
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<SystemErrorLogResponse>>> getSystemErrors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        var response = repository.findAllByOrderByCreatedAtDesc(pageable)
                .map(error -> SystemErrorLogResponse.builder()
                        .id(error.getId())
                        .actorId(error.getActorId())
                        .actorEmail(error.getActorEmail())
                        .exceptionType(error.getExceptionType())
                        .errorMessage(error.getErrorMessage())
                        .requestMethod(error.getRequestMethod())
                        .requestPath(error.getRequestPath())
                        .stackTrace(error.getStackTrace())
                        .createdAt(error.getCreatedAt())
                        .build());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
