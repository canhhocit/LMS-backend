package com.ex.learninghub.modules.course.dto.response;

import com.ex.learninghub.modules.course.entity.Clazz;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ClazzResponse {
    private Long id;
    private String classCode;
    private String className;
    private String semester;
    private String academicYear;
    private Long courseId;
    private String courseTitle;
    private Long lecturerId;
    private String lecturerName;
    private LocalDateTime createdAt;

    public static ClazzResponse from(Clazz clazz) {
        return ClazzResponse.builder()
                .id(clazz.getId())
                .classCode(clazz.getClassCode())
                .className(clazz.getClassName())
                .semester(clazz.getSemester())
                .academicYear(clazz.getAcademicYear())
                .courseId(clazz.getCourse() != null ? clazz.getCourse().getId() : null)
                .courseTitle(clazz.getCourse() != null ? clazz.getCourse().getTitle() : null)
                .lecturerId(clazz.getLecturer() != null ? clazz.getLecturer().getId() : null)
                .lecturerName(clazz.getLecturer() != null ? clazz.getLecturer().getFullName() : null)
                .createdAt(clazz.getCreatedAt())
                .build();
    }
}