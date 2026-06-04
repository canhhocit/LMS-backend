package com.ex.learninghub.modules.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ex.learninghub.modules.course.entity.Course;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByMentorId(Long mentorId);
    List<Course> findByStatus(String status);
}
