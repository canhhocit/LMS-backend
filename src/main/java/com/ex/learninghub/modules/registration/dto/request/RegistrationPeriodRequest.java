package com.ex.learninghub.modules.registration.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationPeriodRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Semester is required")
    @Size(max = 20)
    private String semester;

    @NotBlank(message = "Academic year is required")
    @Size(max = 20)
    private String academicYear;

    @NotNull(message = "Open time is required")
    private LocalDateTime openAt;

    @NotNull(message = "Close time is required")
    private LocalDateTime closeAt;

    @Min(value = 0, message = "Max credits must be >= 0")
    private Integer maxCredits;

    private Boolean isActive;
}
