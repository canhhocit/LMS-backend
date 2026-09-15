package com.ex.learninghub.modules.content.repository;

import com.ex.learninghub.modules.content.entity.StudentVideoNote;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface StudentVideoNoteRepository extends JpaRepository<StudentVideoNote, Long> {
    List<StudentVideoNote> findByUserIdAndLessonIdOrderByTimestampSecondsAsc(Long userId, Long lessonId);
}
