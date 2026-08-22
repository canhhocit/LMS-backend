package com.ex.learninghub.modules.content.dto.response;

import com.ex.learninghub.modules.course.entity.Lesson;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LessonResponse {
    private Long id;
    private String title;
    private String content;
    private String videoUrl;
    private Long chapterId;
    private LocalDateTime createdAt;

    public static LessonResponse from(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .videoUrl(lesson.getVideoUrl())
                .chapterId(lesson.getChapter() != null ? lesson.getChapter().getId() : null)
                .createdAt(lesson.getCreatedAt())
                .build();
    }
}