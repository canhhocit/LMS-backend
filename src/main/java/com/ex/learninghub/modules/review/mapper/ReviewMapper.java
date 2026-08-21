package com.ex.learninghub.modules.review.mapper;

import com.ex.learninghub.modules.review.dto.ReviewDTO;
import com.ex.learninghub.modules.review.entity.Review;
import com.ex.learninghub.modules.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewDTO toDTO(Review review) {
        if (review == null) {
            return null;
        }
        
        return ReviewDTO.builder()
                .id(review.getId())
                .courseId(review.getCourseId())
                .learnerId(review.getLearnerId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public ReviewDTO toDTO(Review review, User learner) {
        if (review == null) {
            return null;
        }
        
        ReviewDTO dto = toDTO(review);
        if (learner != null) {
            dto.setLearnerName(learner.getFullName());
        }
        return dto;
    }

    public Review toEntity(ReviewDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Review.builder()
                .id(dto.getId())
                .courseId(dto.getCourseId())
                .learnerId(dto.getLearnerId())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();
    }
}