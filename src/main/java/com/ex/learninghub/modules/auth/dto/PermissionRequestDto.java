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
public class PermissionRequestDto {
    private Long id;
    private Long lecturerId;
    private String lecturerName;
    private String permissionType;
    private Long classId;
    private String status;
    private String reason;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime validUntil;
    private LocalDateTime createdAt;
}
