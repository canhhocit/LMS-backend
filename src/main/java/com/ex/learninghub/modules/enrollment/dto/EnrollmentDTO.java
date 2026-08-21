package com.ex.learninghub.modules.enrollment.dto;

import com.ex.learninghub.common.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing enrollment details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private Long id;
    private Long learnerId;
    private String learnerName;
    private Long courseId;
    private String courseTitle;
    private EnrollmentStatus status;
    private Double progressPercentage;
    private String enrolledAt;
    private String completedAt;
}