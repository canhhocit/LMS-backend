package com.ex.learninghub.common.enums;

import lombok.Getter;

@Getter
public enum MentorRequestStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String description;

    MentorRequestStatus(String description) {
        this.description = description;
    }
}