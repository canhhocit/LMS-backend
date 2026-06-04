package com.ex.learninghub.modules.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {
    @NotNull(message = "KEY_INVALID")
    private Long courseId;

    @NotNull(message = "KEY_INVALID")
    @Min(value = 1, message = "KEY_INVALID")
    @Max(value = 5, message = "KEY_INVALID")
    private Integer rating;

    private String comment;
}
