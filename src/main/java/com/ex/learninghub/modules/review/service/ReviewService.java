package com.ex.learninghub.modules.review.service;

import com.ex.learninghub.modules.review.dto.CreateReviewDTO;
import com.ex.learninghub.modules.review.dto.ReviewDTO;
import java.util.List;

public interface ReviewService {
    ReviewDTO createReview(Long learnerId, CreateReviewDTO dto);
    ReviewDTO updateReview(Long reviewId, Long learnerId, CreateReviewDTO dto);
    void deleteReview(Long reviewId, Long userId, String role);
    ReviewDTO getReviewById(Long reviewId);
    List<ReviewDTO> getReviewsByCourseId(Long courseId);
    Double getAverageRatingByCourseId(Long courseId);
    Integer getReviewCountByCourseId(Long courseId);
}