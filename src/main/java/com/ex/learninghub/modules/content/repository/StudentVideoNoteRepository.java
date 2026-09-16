package com.ex.learninghub.modules.content.repository;

import com.ex.learninghub.modules.content.entity.StudentVideoNote;
import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentVideoNoteRepository extends JpaRepository<StudentVideoNote, Long> {

    @Query("SELECT n FROM StudentVideoNote n WHERE n.user.id = :userId AND n.lesson.id = :lessonId ORDER BY n.timestampSeconds ASC")
    List<StudentVideoNote> findByUserIdAndLessonIdOrderByTimestampSecondsAsc(@Param("userId") Long userId, @Param("lessonId") Long lessonId);
}
