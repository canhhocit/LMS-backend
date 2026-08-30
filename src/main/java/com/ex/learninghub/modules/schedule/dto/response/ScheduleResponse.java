package com.ex.learninghub.modules.schedule.dto.response;

import com.ex.learninghub.modules.course.entity.ClassSchedule;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResponse {

    private Long id;
    private Long clazzId;
    private String clazzCode;
    private String className;
    private String courseCode;
    private String courseTitle;
    private String lecturerName;
    private Integer dayOfWeek;
    private Integer startPeriod;
    private Integer endPeriod;
    private String room;

    public static ScheduleResponse from(ClassSchedule s) {
        return ScheduleResponse.builder()
                .id(s.getId())
                .clazzId(s.getClazzId())
                .clazzCode(s.getClazz() != null ? s.getClazz().getClassCode() : null)
                .className(s.getClazz() != null ? s.getClazz().getClassName() : null)
                .courseCode(s.getClazz() != null && s.getClazz().getCourse() != null
                        ? s.getClazz().getCourse().getCode() : null)
                .courseTitle(s.getClazz() != null && s.getClazz().getCourse() != null
                        ? s.getClazz().getCourse().getTitle() : null)
                .lecturerName(s.getClazz() != null && s.getClazz().getLecturer() != null
                        ? s.getClazz().getLecturer().getFullName() : null)
                .dayOfWeek(s.getDayOfWeek())
                .startPeriod(s.getStartPeriod())
                .endPeriod(s.getEndPeriod())
                .room(s.getRoom())
                .build();
    }
}
