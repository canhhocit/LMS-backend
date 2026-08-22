package com.ex.learninghub.modules.schedule.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassScheduleRequest {

    @NotNull(message = "Day of week is required")
    @Min(value = 1, message = "Day of week must be between 1 and 7")
    @Max(value = 7, message = "Day of week must be between 1 and 7")
    private Integer dayOfWeek;

    @NotNull(message = "Start period is required")
    @Min(value = 1, message = "Start period must be at least 1")
    @Max(value = 12, message = "Start period must not exceed 12")
    private Integer startPeriod;

    @NotNull(message = "End period is required")
    @Min(value = 1, message = "End period must be at least 1")
    @Max(value = 12, message = "End period must not exceed 12")
    private Integer endPeriod;

    @Size(max = 50, message = "Room must not exceed 50 characters")
    private String room;
}
