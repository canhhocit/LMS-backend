package com.ex.learninghub.modules.auth.dto;

import lombok.Data;

@Data
public class ApprovePermissionRequest {
    private boolean approved;
    private int durationMinutes = 60; // Default 1 hour valid
    private String note;
}
