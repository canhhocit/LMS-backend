package com.ex.learninghub.modules.content.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LessonCommentRequest {
    @NotBlank(message = "Nội dung không được để trống")
    private String content;
    
    private Long parentId;
}