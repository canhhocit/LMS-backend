package com.ex.learninghub.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private Long id;
    private String action;
    private String performedByName;
    private Long classId;
    private Long targetStudentId;
    private String oldValue;
    private String newValue;
    private String approvedByName;
    private LocalDateTime timestamp;
}
