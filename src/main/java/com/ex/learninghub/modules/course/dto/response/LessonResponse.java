package com.ex.learninghub.modules.course.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {
    private Long id;
    private Long chapterId;
    private String title;
    private String content;
    private String videoUrl;
    private Integer duration;
    private Integer sortOrder;
}
