package com.ex.learninghub.modules.enrollment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for enrollment progress information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDTO {
    /** Enrollment identifier */
    private Long enrollmentId;

    /** Total number of lessons in the enrollment */
    private Long totalLessons;

    /** Number of lessons completed */
    private Long completedLessons;

    /** Completion percentage (0‑100) */
    private Double percentage;
}