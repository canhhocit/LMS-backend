package com.ex.learninghub.modules.content.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiLessonSummaryResponse {
    private Long lessonId;
    private String lessonTitle;
    private List<String> keyTakeaways;
    private String summaryText;
    private Integer estimatedStudyMinutes;
}
