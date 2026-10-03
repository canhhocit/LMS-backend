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

    // Optional legacy fields for backward compatibility during migration
    @Size(max = 20)
    private String semester;

    @Size(max = 20)
    private String academicYear;

    // New field for the true academic semester reference
    private Long semesterId;

    @NotNull(message = "Open time is required")
    private LocalDateTime openAt;

    @NotNull(message = "Close time is required")
    private LocalDateTime closeAt;

    @Min(value = 0, message = "Max credits must be >= 0")
    private Integer maxCredits;

    private Boolean isActive;
}

