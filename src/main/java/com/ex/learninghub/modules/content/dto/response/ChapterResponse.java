package com.ex.learninghub.modules.content.dto.response;

import com.ex.learninghub.modules.content.entity.Chapter;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChapterResponse {

    private Long id;
    private Long classId;
    private String title;
    private Integer sortOrder;
    private LocalDateTime createdAt;

    public static ChapterResponse from(Chapter chapter) {
        return ChapterResponse.builder()
                .id(chapter.getId())
                .classId(chapter.getClazz() != null ? chapter.getClazz().getId() : null)
                .title(chapter.getTitle())
                .sortOrder(chapter.getSortOrder())
                .createdAt(chapter.getCreatedAt())
                .build();
    }
}
