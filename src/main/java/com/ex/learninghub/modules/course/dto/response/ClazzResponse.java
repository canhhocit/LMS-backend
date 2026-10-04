package com.ex.learninghub.modules.course.dto.response;

import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.semester.dto.AcademicSemesterResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ClazzResponse {
    private Long id;
    private String classCode;
    private String className;
    // Legacy string fields (backward compat)
    private String semester;
    private String academicYear;
    // New: rich semester object
    private AcademicSemesterResponse academicSemester;
    private Long courseId;
    private String courseTitle;
    private Long lecturerId;
    private String lecturerName;
    private Integer maxStudents;
    private Integer currentStudents;
    private Integer lessonCount;
    private Boolean isGradeLocked;
    private LocalDateTime createdAt;

    public static ClazzResponse from(Clazz clazz) {
        return from(clazz, null, null);
    }

    public static ClazzResponse from(Clazz clazz, Long currentCount) {
        return from(clazz, currentCount, null);
    }

    public static ClazzResponse from(Clazz clazz, Long currentCount, Long lessonCount) {
        return ClazzResponse.builder()
                .id(clazz.getId())
                .classCode(clazz.getClassCode())
                .className(clazz.getClassName())
                .semester(clazz.getEffectiveSemester())
                .academicYear(clazz.getEffectiveAcademicYear())
                .academicSemester(clazz.getAcademicSemester() != null
                        ? AcademicSemesterResponse.from(clazz.getAcademicSemester()) : null)
                .courseId(clazz.getCourse() != null ? clazz.getCourse().getId() : null)
                .courseTitle(clazz.getCourse() != null ? clazz.getCourse().getTitle() : null)
                .lecturerId(clazz.getLecturer() != null ? clazz.getLecturer().getId() : null)
                .lecturerName(clazz.getLecturer() != null ? clazz.getLecturer().getFullName() : null)
                .maxStudents(clazz.getMaxStudents())
                .currentStudents(currentCount != null ? currentCount.intValue() : 0)
                .lessonCount(lessonCount != null ? lessonCount.intValue() : 0)
                .isGradeLocked(Boolean.TRUE.equals(clazz.getIsGradeLocked()))
                .createdAt(clazz.getCreatedAt())
                .build();
    }
}