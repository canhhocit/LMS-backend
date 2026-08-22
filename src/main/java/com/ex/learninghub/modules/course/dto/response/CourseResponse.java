package com.ex.learninghub.modules.course.dto.response;

import com.ex.learninghub.modules.course.entity.Course;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CourseResponse {
    private Long id;
    private String code;
    private String title;
    private String description;
    private Integer credit;
    private LocalDateTime createdAt;

    public static CourseResponse from(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .code(course.getCode())
                .title(course.getTitle())
                .description(course.getDescription())
                .credit(course.getCredit())
                .createdAt(course.getCreatedAt())
                .build();
    }
}