package com.ex.learninghub.modules.schedule.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiScheduleRecommendRequest {
    private String semester;
    private String academicYear;
    private List<Long> desiredCourseIds;
    private Integer maxCredits;
    private List<Integer> preferOffDays; // 1=Mon, 6=Sat, 7=Sun
    private Boolean avoidEarlyMorning; // true = avoid startPeriod <= 2
    private String customPreference; // e.g. "Muốn học gọn trong 3 ngày giữa tuần"
}
