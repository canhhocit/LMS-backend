package com.ex.learninghub.modules.review.repository;

import com.ex.learninghub.modules.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByCourseId(Long courseId);

    Optional<Review> findByCourseIdAndLearnerId(Long courseId, Long learnerId);

    boolean existsByCourseIdAndLearnerId(Long courseId, Long learnerId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.courseId = :courseId")
    Double getAverageRatingByCourseId(Long courseId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.courseId = :courseId")
    long countByCourseId(Long courseId);
}