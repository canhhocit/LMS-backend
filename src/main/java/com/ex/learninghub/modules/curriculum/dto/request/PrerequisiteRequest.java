package com.ex.learninghub.modules.curriculum.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrerequisiteRequest {
    @NotNull
    private Long prerequisiteCourseId;
}
