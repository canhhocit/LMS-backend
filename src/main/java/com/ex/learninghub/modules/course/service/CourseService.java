package com.ex.learninghub.modules.course.service;

import com.ex.learninghub.modules.course.dto.response.CourseResponse;
import com.ex.learninghub.modules.course.dto.response.ChapterResponse;
import com.ex.learninghub.modules.course.dto.response.LessonResponse;
import java.util.List;

public interface CourseService {
    CourseResponse createCourse(String title, String description, java.math.BigDecimal price, String email);
    CourseResponse getCourseById(Long id);
    List<CourseResponse> getAllCourses();
    
    ChapterResponse createChapter(Long courseId, String title, Integer sortOrder);
    List<ChapterResponse> getChaptersByCourse(Long courseId);

    LessonResponse createLesson(Long chapterId, String title, String content, String videoUrl, Integer duration, Integer sortOrder);
    List<LessonResponse> getLessonsByChapter(Long chapterId);
}
