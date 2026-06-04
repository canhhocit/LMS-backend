package com.ex.learninghub.modules.course.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.course.dto.response.ChapterResponse;
import com.ex.learninghub.modules.course.dto.response.CourseResponse;
import com.ex.learninghub.modules.course.dto.response.LessonResponse;
import com.ex.learninghub.modules.course.entity.Chapter;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.entity.Lesson;
import com.ex.learninghub.modules.course.repository.ChapterRepository;
import com.ex.learninghub.modules.course.repository.CourseRepository;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import com.ex.learninghub.modules.course.service.CourseService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CourseResponse createCourse(String title, String description, java.math.BigDecimal price, String email) {
        User mentor = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Course course = Course.builder()
                .title(title)
                .description(description)
                .price(price)
                .mentor(mentor)
                .status("DRAFT")
                .build();

        course = courseRepository.save(course);
        return mapToCourseResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        return mapToCourseResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChapterResponse createChapter(Long courseId, String title, Integer sortOrder) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        Chapter chapter = Chapter.builder()
                .course(course)
                .title(title)
                .sortOrder(sortOrder)
                .build();

        chapter = chapterRepository.save(chapter);
        return mapToChapterResponse(chapter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponse> getChaptersByCourse(Long courseId) {
        return chapterRepository.findByCourseIdOrderBySortOrderAsc(courseId).stream()
                .map(this::mapToChapterResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LessonResponse createLesson(Long chapterId, String title, String content, String videoUrl, Integer duration, Integer sortOrder) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        Lesson lesson = Lesson.builder()
                .chapter(chapter)
                .title(title)
                .content(content)
                .videoUrl(videoUrl)
                .duration(duration)
                .sortOrder(sortOrder)
                .build();

        lesson = lessonRepository.save(lesson);
        return mapToLessonResponse(lesson);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> getLessonsByChapter(Long chapterId) {
        return lessonRepository.findByChapterIdOrderBySortOrderAsc(chapterId).stream()
                .map(this::mapToLessonResponse)
                .collect(Collectors.toList());
    }

    private CourseResponse mapToCourseResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .mentorId(course.getMentor().getId())
                .status(course.getStatus())
                .build();
    }

    private ChapterResponse mapToChapterResponse(Chapter chapter) {
        return ChapterResponse.builder()
                .id(chapter.getId())
                .courseId(chapter.getCourse().getId())
                .title(chapter.getTitle())
                .sortOrder(chapter.getSortOrder())
                .build();
    }

    private LessonResponse mapToLessonResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .chapterId(lesson.getChapter().getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .videoUrl(lesson.getVideoUrl())
                .duration(lesson.getDuration())
                .sortOrder(lesson.getSortOrder())
                .build();
    }
}
