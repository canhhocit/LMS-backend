package com.ex.learninghub.modules.review.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.repository.CourseRepository;
import com.ex.learninghub.modules.review.dto.request.ReviewCreateRequest;
import com.ex.learninghub.modules.review.dto.response.ReviewResponse;
import com.ex.learninghub.modules.review.entity.Review;
import com.ex.learninghub.modules.review.repository.ReviewRepository;
import com.ex.learninghub.modules.review.service.ReviewService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request, String email) {
        User learner = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (reviewRepository.existsByCourseIdAndLearnerId(course.getId(), learner.getId())) {
            throw new AppException(ErrorCode.REVIEW_EXISTS);
        }

        Review review = Review.builder()
                .course(course)
                .learner(learner)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);
        return mapToReviewResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }
        return reviewRepository.findByCourseId(courseId).stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .courseId(review.getCourse().getId())
                .learnerId(review.getLearner().getId())
                .learnerName(review.getLearner().getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
