package com.ex.learninghub.modules.learning.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponse {
    private Long id;
    private Long enrollmentId;
    private Long lessonId;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
}
