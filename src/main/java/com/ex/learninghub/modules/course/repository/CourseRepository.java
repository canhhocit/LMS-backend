package com.ex.learninghub.modules.course.repository;

import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.common.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findByStatus(CourseStatus status, Pageable pageable);

    Page<Course> findByStatusIn(List<CourseStatus> statuses, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.status = :status AND " +
           "(LOWER(c.courseName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Course> searchByKeywordAndStatus(
            @Param("keyword") String keyword,
            @Param("status") CourseStatus status,
            Pageable pageable);

    List<Course> findByMentorId(Long mentorId);

    Optional<Course> findByCourseCode(String courseCode);

    boolean existsByCourseCode(String courseCode);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.status = :status")
    long countByStatus(@Param("status") CourseStatus status);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.chapters WHERE c.id = :id")
    Optional<Course> findByIdWithChapters(@Param("id") Long id);
}