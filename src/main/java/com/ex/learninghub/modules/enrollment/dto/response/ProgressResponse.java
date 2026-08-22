package com.ex.learninghub.modules.enrollment.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ProgressResponse {
    private Long enrollmentId;
    private Long clazzId;
    private long completedCount;
    private long totalCount;
    private double percentage;
    private List<LessonProgressItem> lessons;
}
