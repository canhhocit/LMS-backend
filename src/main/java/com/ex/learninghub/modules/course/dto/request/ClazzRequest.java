package com.ex.learninghub.modules.course.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for creating or updating a class (Clazz).
 */
@Getter
@Setter
@NoArgsConstructor
public class ClazzRequest {

    @NotBlank(message = "Class code is required")
    private String classCode;

    @NotBlank(message = "Class name is required")
    private String className;

    private String semester;

    private String academicYear;

    private Long semesterId;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDateTime examDate;

    private String examRoom;

    private String examFormat;

    private Integer examDuration;


    @NotNull(message = "Course ID is required")
    private Long courseId;

    private Long lecturerId;

    @Positive(message = "Max students must be greater than 0")
    private Integer maxStudents;
}
