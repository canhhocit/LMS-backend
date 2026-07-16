package com.ex.learninghub.modules.course.dto.response;

import com.ex.learninghub.modules.course.entity.Course;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CourseResponse {

    private Long id;
    private String courseCode;
    private String courseName;
    private Integer credits;
    private String description;
    private LocalDateTime createdAt;

    public static CourseResponse from(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .credits(course.getCredits())
                .description(course.getDescription())
                .createdAt(course.getCreatedAt())
                .build();
    }
}
