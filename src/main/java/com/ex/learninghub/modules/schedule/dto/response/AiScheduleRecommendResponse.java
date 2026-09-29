package com.ex.learninghub.modules.schedule.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiScheduleRecommendResponse {
    private String studentName;
    private String semester;
    private String academicYear;
    private String summaryAdvice;
    private List<AiScheduleOption> options;
}
