package com.ex.learninghub.modules.registration.dto.response;

import com.ex.learninghub.modules.registration.entity.RegistrationPeriod;
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
    private String semester;
    private String academicYear;
    private LocalDateTime openAt;
    private LocalDateTime closeAt;
    private Integer maxCredits;
    private Boolean isActive;

    public static RegistrationPeriodResponse from(RegistrationPeriod p) {
        return RegistrationPeriodResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .semester(p.getSemester())
                .academicYear(p.getAcademicYear())
                .openAt(p.getOpenAt())
                .closeAt(p.getCloseAt())
                .maxCredits(p.getMaxCredits())
                .isActive(p.getIsActive())
                .build();
    }
}
