package com.ex.learninghub.common.enums;

import lombok.Getter;

@Getter
public enum CourseStatus {
    DRAFT("Draft"),
    PENDING_REVIEW("Pending Review"),
    PUBLISHED("Published"),
    ARCHIVED("Archived");

    private final String description;

    CourseStatus(String description) {
        this.description = description;
    }
}