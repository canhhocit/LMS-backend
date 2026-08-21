package com.ex.learninghub.common.enums;

import lombok.Getter;

@Getter
public enum EnrollmentStatus {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String description;

    EnrollmentStatus(String description) {
        this.description = description;
    }
}