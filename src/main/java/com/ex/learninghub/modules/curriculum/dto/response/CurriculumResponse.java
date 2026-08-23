package com.ex.learninghub.modules.curriculum.dto.response;

import com.ex.learninghub.modules.curriculum.entity.Curriculum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurriculumResponse {
    private Long id;
    private String name;
    private String faculty;
    private String academicYear;
    private Boolean isActive;

    public static CurriculumResponse from(Curriculum c) {
        return CurriculumResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .faculty(c.getFaculty())
                .academicYear(c.getAcademicYear())
                .isActive(c.getIsActive())
                .build();
    }
}
