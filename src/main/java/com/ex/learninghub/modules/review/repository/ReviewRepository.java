package com.ex.learninghub.modules.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ex.learninghub.modules.review.entity.Review;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCourseId(Long courseId);
    boolean existsByCourseIdAndLearnerId(Long courseId, Long learnerId);
}
