package com.ex.learninghub.modules.course.dto.response;

import com.ex.learninghub.common.enums.CourseStatus;
import com.ex.learninghub.modules.course.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for returning course information.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private Long id;
    private String courseCode;
    private String courseName;
    private Integer credits;
    private String description;
    private LocalDateTime createdAt;
    
    // New fields for SRS Online Learning Platform
    private Long mentorId;
    private String mentorName;
    private BigDecimal price;
    private CourseStatus status;
    private String thumbnail;
    private Double averageRating;
    private Integer totalReviews;
    private Integer totalStudents;
    private List<ChapterResponse> chapters;

    public static CourseResponse from(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .credits(course.getCredits())
                .description(course.getDescription())
                .createdAt(course.getCreatedAt())
                .mentorId(course.getMentorId())
                .price(course.getPrice())
                .status(course.getStatus())
                .thumbnail(course.getThumbnail())
                .build();
    }
    
    // Builder pattern with all fields
    public static CourseResponse buildFull(Course course, String mentorName, Double avgRating, 
            Integer totalReviews, Integer totalStudents, List<ChapterResponse> chapters) {
        return CourseResponse.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .credits(course.getCredits())
                .description(course.getDescription())
                .createdAt(course.getCreatedAt())
                .mentorId(course.getMentorId())
                .mentorName(mentorName)
                .price(course.getPrice())
                .status(course.getStatus())
                .thumbnail(course.getThumbnail())
                .averageRating(avgRating)
                .totalReviews(totalReviews)
                .totalStudents(totalStudents)
                .chapters(chapters)
                .build();
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChapterResponse {
        private Long id;
        private String title;
        private Integer sortOrder;
        private List<LessonResponse> lessons;
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonResponse {
        private Long id;
        private String title;
        private String content;
        private String videoUrl;
        private Integer duration;
        private Integer sortOrder;
    }
}
