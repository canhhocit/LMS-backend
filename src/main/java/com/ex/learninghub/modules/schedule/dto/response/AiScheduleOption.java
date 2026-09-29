package com.ex.learninghub.modules.schedule.dto.response;

import com.ex.learninghub.modules.course.dto.response.ClazzResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiScheduleOption {
    private String optionId;
    private String title;
    private Integer totalCredits;
    private Double matchScore; // e.g. 95.0
    private String reasoning; // AI explanation for this timetable
    private List<ClazzResponse> suggestedClasses;
    private List<ScheduleResponse> scheduleDetails;
}
