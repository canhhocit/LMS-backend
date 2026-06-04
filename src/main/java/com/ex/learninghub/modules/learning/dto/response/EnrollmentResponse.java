package com.ex.learninghub.modules.learning.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long id;
    private Long learnerId;
    private Long courseId;
    private String status;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
}
