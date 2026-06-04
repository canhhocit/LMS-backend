package com.ex.learninghub.modules.course.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.course.dto.response.ChapterResponse;
import com.ex.learninghub.modules.course.dto.response.CourseResponse;
import com.ex.learninghub.modules.course.dto.response.LessonResponse;
import com.ex.learninghub.modules.course.service.CourseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Course Module", description = "Endpoints for managing Courses, Chapters, and Lessons")
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/courses")
    @Operation(summary = "Create a new course (Mentor only)")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") BigDecimal price,
            @AuthenticationPrincipal UserDetails userDetails) {
        CourseResponse response = courseService.createCourse(title, description, price, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CourseResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Course created successfully")
                        .result(response)
                        .build()
        );
    }

    @GetMapping("/courses/{id}")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable Long id) {
        CourseResponse response = courseService.getCourseById(id);
        return ResponseEntity.ok(
                ApiResponse.<CourseResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Course retrieved successfully")
                        .result(response)
                        .build()
        );
    }

    @GetMapping("/courses")
    @Operation(summary = "Get all courses")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses() {
        List<CourseResponse> response = courseService.getAllCourses();
        return ResponseEntity.ok(
                ApiResponse.<List<CourseResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Courses retrieved successfully")
                        .result(response)
                        .build()
        );
    }

    @PostMapping("/courses/{id}/chapters")
    @Operation(summary = "Add a new chapter to a course")
    public ResponseEntity<ApiResponse<ChapterResponse>> createChapter(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam Integer sortOrder) {
        ChapterResponse response = courseService.createChapter(id, title, sortOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ChapterResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Chapter created successfully")
                        .result(response)
                        .build()
        );
    }

    @GetMapping("/courses/{id}/chapters")
    @Operation(summary = "Get all chapters of a course")
    public ResponseEntity<ApiResponse<List<ChapterResponse>>> getChaptersByCourse(@PathVariable Long id) {
        List<ChapterResponse> response = courseService.getChaptersByCourse(id);
        return ResponseEntity.ok(
                ApiResponse.<List<ChapterResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Chapters retrieved successfully")
                        .result(response)
                        .build()
        );
    }

    @PostMapping("/chapters/{chapterId}/lessons")
    @Operation(summary = "Add a new lesson to a chapter")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(
            @PathVariable Long chapterId,
            @RequestParam String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String videoUrl,
            @RequestParam(required = false) Integer duration,
            @RequestParam Integer sortOrder) {
        LessonResponse response = courseService.createLesson(chapterId, title, content, videoUrl, duration, sortOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<LessonResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Lesson created successfully")
                        .result(response)
                        .build()
        );
    }

    @GetMapping("/chapters/{chapterId}/lessons")
    @Operation(summary = "Get all lessons of a chapter")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getLessonsByChapter(@PathVariable Long chapterId) {
        List<LessonResponse> response = courseService.getLessonsByChapter(chapterId);
        return ResponseEntity.ok(
                ApiResponse.<List<LessonResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lessons retrieved successfully")
                        .result(response)
                        .build()
        );
    }
}
