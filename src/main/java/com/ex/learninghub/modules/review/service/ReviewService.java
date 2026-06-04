package com.ex.learninghub.modules.review.service;

import com.ex.learninghub.modules.review.dto.request.ReviewCreateRequest;
import com.ex.learninghub.modules.review.dto.response.ReviewResponse;
import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewCreateRequest request, String email);
    List<ReviewResponse> getReviewsByCourse(Long courseId);
}
