package com.ex.learninghub.modules.enrollment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new enrollment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEnrollmentDTO {
    private Long learnerId;
    private Long courseId;
}