package com.ex.learninghub.modules.quiz.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAdvisorRequest {
    private Long studentId;
    private Long courseId;
    private String customQuery;
}
