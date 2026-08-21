package com.ex.learninghub.modules.course.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @NotNull(message = "Course ID is required")
    private Long courseId;

    private Long lecturerId;
}
