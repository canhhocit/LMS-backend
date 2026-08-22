package com.ex.learninghub.modules.course.service;

import com.ex.learninghub.modules.course.dto.request.CourseRequest;
import com.ex.learninghub.modules.course.dto.response.CourseResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {

    CourseResponse createCourse(CourseRequest request);

    CourseResponse updateCourse(Long id, CourseRequest request);

    void deleteCourse(Long id);

    List<CourseResponse> getAllCourses();

    Page<CourseResponse> getAllCourses(Pageable pageable);

    CourseResponse getCourseById(Long id);
}
