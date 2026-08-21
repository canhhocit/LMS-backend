package com.ex.learninghub.modules.course.dto.request;

import com.ex.learninghub.common.enums.CourseStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for creating or updating a course.
 */
@Getter
@Setter
@NoArgsConstructor
public class CourseRequest {

    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotBlank(message = "Course name is required")
    private String courseName;

    @NotNull(message = "Credits is required")
    @Min(value = 1, message = "Credits must be at least 1")
    private Integer credits;

    private String description;

    // New fields for SRS Online Learning Platform
    private Long mentorId;

    @Min(value = 0, message = "Price must be at least 0")
    private BigDecimal price = BigDecimal.ZERO;

    private CourseStatus status = CourseStatus.DRAFT;

    private String thumbnail;
}
