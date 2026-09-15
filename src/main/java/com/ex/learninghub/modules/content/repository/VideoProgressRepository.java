package com.ex.learninghub.modules.content.repository;

import com.ex.learninghub.modules.content.entity.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface VideoProgressRepository extends JpaRepository<VideoProgress, Long> {
    Optional<VideoProgress> findByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);
}
