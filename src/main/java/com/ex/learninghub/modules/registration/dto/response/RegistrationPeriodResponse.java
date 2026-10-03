package com.ex.learninghub.modules.registration.dto.response;

import com.ex.learninghub.modules.registration.entity.RegistrationPeriod;
import com.ex.learninghub.modules.semester.dto.AcademicSemesterResponse;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationPeriodResponse {
    private Long id;
    private String name;
    // Legacy fields
    private String semester;
    private String academicYear;
    // New field
    private AcademicSemesterResponse academicSemester;
    private LocalDateTime openAt;
    private LocalDateTime closeAt;
    private Integer maxCredits;
    private Boolean isActive;

    public static RegistrationPeriodResponse from(RegistrationPeriod p) {
        return RegistrationPeriodResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .semester(p.getEffectiveSemester())
                .academicYear(p.getEffectiveAcademicYear())
                .academicSemester(p.getAcademicSemester() != null
                        ? AcademicSemesterResponse.from(p.getAcademicSemester()) : null)
                .openAt(p.getOpenAt())
                .closeAt(p.getCloseAt())
                .maxCredits(p.getMaxCredits())
                .isActive(p.getIsActive())
                .build();
    }
}

