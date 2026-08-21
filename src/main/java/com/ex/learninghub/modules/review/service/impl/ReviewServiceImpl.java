package com.ex.learninghub.modules.review.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.enrollment.entity.Enrollment;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.common.enums.EnrollmentStatus;
import com.ex.learninghub.modules.review.dto.CreateReviewDTO;
import com.ex.learninghub.modules.review.dto.ReviewDTO;
import com.ex.learninghub.modules.review.entity.Review;
import com.ex.learninghub.modules.review.mapper.ReviewMapper;
import com.ex.learninghub.modules.review.repository.ReviewRepository;
import com.ex.learninghub.modules.review.service.ReviewService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewDTO createReview(Long learnerId, CreateReviewDTO dto) {
        // Check if learner has completed the course
        Enrollment enrollment = enrollmentRepository.findByLearnerIdAndCourseId(learnerId, dto.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        if (enrollment.getStatusook() != EnrollmentStatus.COMPLETED) {
            throw new AppException(ErrorCode.REVIEW_NOT_ALLOWED);
        }

        // Check if review already exists
        if (reviewRepository.existsByCourseIdAndLearnerId(dto.getCourseId(), learnerId)) {
            throw new AppException(ErrorCode.REVIEW_EXISTS);
        }

        Review review = Review.builder()
                .courseId(dto.getCourseId())
                .learnerId(learnerId)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();

        review = reviewRepository.save(review);
        
        User learner = userRepository.findById(learnerId).orElse(null);
        return reviewMapper.toDTO(review, learner);
    }

    @Override
    @Transactional
    public ReviewDTO updateReview(Long reviewId, Long learnerId, CreateReviewDTO dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getLearnerId().equals(learnerId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        
        review = reviewRepository.save(review);
        
        User learner = userRepository.findById(learnerId).orElse(null);
        return reviewMapper.toDTO(review, learner);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId, String role) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        // Only the review owner or ADMIN can delete
        if (!review.getLearnerId().equals(userId) && !role.equals("ADMIN")) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        reviewRepository.delete(review);
    }

    @Override
    public ReviewDTO getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));
        
        User learner = userRepository.findById(review.getLearnerId()).orElse(null);
        return reviewMapper.toDTO(review, learner);
    }

    @Override
    public List<ReviewDTO> getReviewsByCourseId(Long courseId) {
        List<Review> reviews = reviewRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
        
        return reviews.stream()
                .map(review -> {
                    User learner = userRepository.findById(review.getLearnerId()).orElse(null);
                    return reviewMapper.toDTO(review, learner);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageRatingByCourseId(Long courseId) {
        return reviewRepository.getAverageRatingByCourseId(courseId);
    }

    @Override
    public Integer getReviewCountByCourseId(Long courseId) {
        return reviewRepository.countByCourseId(courseId);
    }
}