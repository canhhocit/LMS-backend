package com.ex.learninghub.modules.content.repository;

import com.ex.learninghub.modules.content.entity.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoProgressRepository extends JpaRepository<VideoProgress, Long> {
    Optional<VideoProgress> findByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);
}
