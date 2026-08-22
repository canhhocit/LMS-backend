package com.ex.learninghub.modules.enrollment.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LessonProgressItem {
    private Long lessonId;
    private String lessonTitle;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
}
