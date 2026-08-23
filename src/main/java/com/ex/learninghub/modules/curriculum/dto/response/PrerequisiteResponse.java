package com.ex.learninghub.modules.curriculum.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrerequisiteResponse {
    private Long id;
    private Long courseId;
    private Long prerequisiteCourseId;
    private String prerequisiteCourseCode;
    private String prerequisiteCourseTitle;
}
