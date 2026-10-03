package com.ex.learninghub.modules.semester.dto;

import com.ex.learninghub.modules.semester.entity.SemesterStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicSemesterRequest {
    private String code;
    private String name;
    private String academicYear;
    private Integer semesterNo;
    private LocalDate startDate;
    private LocalDate endDate;
    private SemesterStatus status;
}
