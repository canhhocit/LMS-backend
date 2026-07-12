package com.ex.learninghub.modules.content.repository;

import com.ex.learninghub.modules.content.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByClazzIdOrderBySortOrderAsc(Long clazzId);
}
