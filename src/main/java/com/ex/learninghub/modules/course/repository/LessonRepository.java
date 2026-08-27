package com.ex.learninghub.modules.course.repository;

import com.ex.learninghub.modules.course.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByChapterIdOrderBySortOrderAsc(Long chapterId);

    @Query("SELECT l FROM Lesson l WHERE l.chapterId = :chapterId ORDER BY l.sortOrder")
    List<Lesson> findLessonsByChapterIdOrdered(@Param("chapterId") Long chapterId);

    @Query("SELECT l FROM Lesson l JOIN Chapter c ON c.id = l.chapterId " +
           "WHERE c.clazzId = :clazzId ORDER BY c.sortOrder, l.sortOrder")
    List<Lesson> findByClazzId(@Param("clazzId") Long clazzId);

    @Query("SELECT l FROM Lesson l WHERE l.chapterId IN " +
           "(SELECT c.id FROM Chapter c WHERE c.clazzId = :clazzId)")
    List<Lesson> findByChapterClazzId(@Param("clazzId") Long clazzId);

    Optional<Lesson> findByChapterIdAndSortOrder(Long chapterId, Integer sortOrder);

    void deleteByChapterId(Long chapterId);

    long countByChapterId(Long chapterId);

    Page<Lesson> findByChapterId(Long chapterId, Pageable pageable);
}
