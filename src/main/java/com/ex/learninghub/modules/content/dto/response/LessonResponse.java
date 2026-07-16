package com.ex.learninghub.modules.content.dto.response;

import com.ex.learninghub.modules.content.entity.Lesson;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LessonResponse {

    private Long id;
    private Long chapterId;
    private String title;
    private String content;
    private String videoUrl;
    private LocalDateTime createdAt;

    public static LessonResponse from(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .chapterId(lesson.getChapter() != null ? lesson.getChapter().getId() : null)
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .videoUrl(lesson.getVideoUrl())
                .createdAt(lesson.getCreatedAt())
                .build();
    }
}
