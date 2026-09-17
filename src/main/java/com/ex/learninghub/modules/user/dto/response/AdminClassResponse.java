package com.ex.learninghub.modules.user.dto.response;

import com.ex.learninghub.modules.user.entity.AdministrativeClass;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminClassResponse {

    private Long id;
    private String className;
    private String faculty;
    private String academicYear;
    private Long curriculumId;
    private String curriculumName;
    private Integer studentCount;
    private LocalDateTime createdAt;

    public static AdminClassResponse from(AdministrativeClass ac, int studentCount) {
        return AdminClassResponse.builder()
                .id(ac.getId())
                .className(ac.getClassName())
                .faculty(ac.getFaculty())
                .academicYear(ac.getAcademicYear())
                .curriculumId(ac.getCurriculum() != null ? ac.getCurriculum().getId() : null)
                .curriculumName(ac.getCurriculum() != null ? ac.getCurriculum().getName() : null)
                .studentCount(studentCount)
                .createdAt(ac.getCreatedAt())
                .build();
    }

    public static AdminClassResponse from(AdministrativeClass ac) {
        return from(ac, 0);
    }
}
