package com.ex.learninghub.modules.course.repository;

import com.ex.learninghub.modules.course.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByChapterIdOrderBySortOrderAsc(Long chapterId);

    @Query("SELECT l FROM Lesson l WHERE l.chapterId = :chapterId ORDER BY l.sortOrder")
    List<Lesson> findLessonsByChapterIdOrdered(@Param("chapterId") Long chapterId);

    @Query("SELECT l FROM Lesson l WHERE l.chapterId IN " +
           "(SELECT c.id FROM Chapter c WHERE c.courseId = :courseId) " +
           "ORDER BY c.sortOrder, l.sortOrder")
    List<Lesson> findByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT l FROM Lesson l WHERE l.chapterId IN " +
           "(SELECT c.id FROM Chapter c WHERE c.courseId = :courseId)")
    List<Lesson> findByChapterCourseId(@Param("courseId") Long courseId);

    Optional<Lesson> findByChapterIdAndSortOrder(Long chapterId, Integer sortOrder);

    void deleteByChapterId(Long chapterId);

    long countByChapterId(Long chapterId);
}