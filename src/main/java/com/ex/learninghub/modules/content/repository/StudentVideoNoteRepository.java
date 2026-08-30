package com.ex.learninghub.modules.content.repository;

import com.ex.learninghub.modules.content.entity.StudentVideoNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentVideoNoteRepository extends JpaRepository<StudentVideoNote, Long> {
    List<StudentVideoNote> findByUserIdAndLessonIdOrderByTimestampSecondsAsc(Long userId, Long lessonId);
}
