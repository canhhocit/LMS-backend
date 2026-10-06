package com.ex.learninghub.modules.content.dto.response;

import com.ex.learninghub.modules.content.entity.VideoProgress;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VideoProgressResponse(
        Long id,
        Long enrollmentId,
        Long lessonId,
        BigDecimal lastWatchedSeconds,
        BigDecimal maxWatchedSeconds,
        boolean completed,
        LocalDateTime updatedAt
) {
    public static VideoProgressResponse from(VideoProgress progress) {
        return new VideoProgressResponse(
                progress.getId(),
                progress.getEnrollment().getId(),
                progress.getLesson().getId(),
                progress.getLastWatchedSeconds(),
                progress.getMaxWatchedSeconds(),
                Boolean.TRUE.equals(progress.getIsCompleted()),
                progress.getUpdatedAt()
        );
    }
}
