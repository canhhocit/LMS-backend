package com.ex.learninghub.modules.semester.dto;

import com.ex.learninghub.modules.semester.entity.AcademicSemester;
import com.ex.learninghub.modules.semester.entity.SemesterStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicSemesterResponse {
    private Long id;
    private String code;
    private String name;
    private String academicYear;
    private Integer semesterNo;
    private LocalDate startDate;
    private LocalDate endDate;
    private SemesterStatus status;
    private String shortLabel;

    public static AcademicSemesterResponse from(AcademicSemester s) {
        return AcademicSemesterResponse.builder()
                .id(s.getId())
                .code(s.getCode())
                .name(s.getName())
                .academicYear(s.getAcademicYear())
                .semesterNo(s.getSemesterNo())
                .startDate(s.getStartDate())
                .endDate(s.getEndDate())
                .status(s.getStatus())
                .shortLabel(s.getShortLabel())
                .build();
    }
}
