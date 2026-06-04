package com.ex.learninghub.modules.course.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterResponse {
    private Long id;
    private Long courseId;
    private String title;
    private Integer sortOrder;
}
