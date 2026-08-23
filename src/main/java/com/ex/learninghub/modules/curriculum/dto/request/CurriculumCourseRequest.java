package com.ex.learninghub.modules.curriculum.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurriculumCourseRequest {
    @NotNull
    private Long courseId;

    @NotNull
    @Min(1)
    @Max(20)
    private Integer semesterNo;

    private Boolean isRequired;
}
