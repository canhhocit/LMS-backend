package com.ex.learninghub.modules.content.dto.response;

import com.ex.learninghub.modules.content.entity.LessonComment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LessonCommentResponse {
    private Long id;
    private Long lessonId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String content;
    private Long parentId;
    private LocalDateTime createdAt;
    
    public static LessonCommentResponse from(LessonComment entity) {
        return LessonCommentResponse.builder()
                .id(entity.getId())
                .lessonId(entity.getLessonId())
                .userId(entity.getUser().getId())
                .userName(entity.getUser().getFullName())
                .userAvatar(entity.getUser().getAvatarUrl())
                .content(entity.getContent())
                .parentId(entity.getParentId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}