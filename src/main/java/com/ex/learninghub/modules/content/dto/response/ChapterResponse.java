package com.ex.learninghub.modules.content.dto.response;

import com.ex.learninghub.modules.course.entity.Chapter;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChapterResponse {
    private Long id;
    private String title;
    private Integer sortOrder;
    private Long clazzId;
    private LocalDateTime createdAt;

    public static ChapterResponse from(Chapter chapter) {
        return ChapterResponse.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .sortOrder(chapter.getSortOrder())
                .clazzId(chapter.getClazz() != null ? chapter.getClazz().getId() : null)
                .createdAt(chapter.getCreatedAt())
                .build();
    }
}