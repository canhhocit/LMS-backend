package com.ex.learninghub.modules.curriculum.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurriculumRequest {
    @NotBlank
    @Size(max = 150)
    private String name;

    @Size(max = 100)
    private String faculty;

    @NotBlank
    @Size(max = 20)
    private String academicYear;

    private Boolean isActive;
}
