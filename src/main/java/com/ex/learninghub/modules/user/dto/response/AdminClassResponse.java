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
    private Long homeroomTeacherId;
    private String homeroomTeacherName;
    private String homeroomTeacherCode;
    private String advisorName;
    private Integer studentCount;
    private LocalDateTime createdAt;

    public static AdminClassResponse from(AdministrativeClass ac, int studentCount) {
        String teacherName = ac.getHomeroomTeacher() != null ? ac.getHomeroomTeacher().getFullName() : null;
        String teacherCode = ac.getHomeroomTeacher() != null ? ac.getHomeroomTeacher().getLecturerCode() : null;
        Long teacherId = ac.getHomeroomTeacher() != null ? ac.getHomeroomTeacher().getId() : null;

        return AdminClassResponse.builder()
                .id(ac.getId())
                .className(ac.getClassName())
                .faculty(ac.getFaculty())
                .academicYear(ac.getAcademicYear())
                .curriculumId(ac.getCurriculum() != null ? ac.getCurriculum().getId() : null)
                .curriculumName(ac.getCurriculum() != null ? ac.getCurriculum().getName() : null)
                .homeroomTeacherId(teacherId)
                .homeroomTeacherName(teacherName)
                .homeroomTeacherCode(teacherCode)
                .advisorName(teacherName)
                .studentCount(studentCount)
                .createdAt(ac.getCreatedAt())
                .build();
    }

    public static AdminClassResponse from(AdministrativeClass ac) {
        return from(ac, 0);
    }
}
