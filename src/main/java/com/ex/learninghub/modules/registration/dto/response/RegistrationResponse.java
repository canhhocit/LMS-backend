package com.ex.learninghub.modules.registration.dto.response;

import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.enrollment.entity.Enrollment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationResponse {
    private Long enrollmentId;
    private Long clazzId;
    private String clazzCode;
    private String courseCode;
    private String courseTitle;
    private Integer credits;
    private LocalDateTime enrolledAt;

    public static RegistrationResponse from(Enrollment e) {
        Clazz c = e.getClazz();
        Course course = c != null ? c.getCourse() : null;
        return RegistrationResponse.builder()
                .enrollmentId(e.getId())
                .clazzId(c != null ? c.getId() : null)
                .clazzCode(c != null ? c.getClassCode() : null)
                .courseCode(course != null ? course.getCode() : null)
                .courseTitle(course != null ? course.getTitle() : null)
                .credits(course != null ? course.getCredit() : null)
                .enrolledAt(e.getEnrolledAt() != null ? e.getEnrolledAt() : LocalDateTime.now())
                .build();
    }
}
