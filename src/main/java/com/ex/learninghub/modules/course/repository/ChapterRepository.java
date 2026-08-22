package com.ex.learninghub.modules.course.repository;

import com.ex.learninghub.modules.course.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    List<Chapter> findByClazzIdOrderBySortOrderAsc(Long clazzId);

    @Query("SELECT c FROM Chapter c LEFT JOIN FETCH c.lessons WHERE c.id = :id")
    Optional<Chapter> findByIdWithLessons(@Param("id") Long id);

    @Query("SELECT c FROM Chapter c LEFT JOIN FETCH c.lessons WHERE c.clazzId = :clazzId ORDER BY c.sortOrder")
    List<Chapter> findByClazzIdWithLessons(@Param("clazzId") Long clazzId);

    void deleteByClazzId(Long clazzId);

    Page<Chapter> findByClazzId(Long clazzId, Pageable pageable);
}