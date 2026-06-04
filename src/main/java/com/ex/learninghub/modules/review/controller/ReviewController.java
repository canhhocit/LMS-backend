package com.ex.learninghub.modules.review.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.review.dto.request.ReviewCreateRequest;
import com.ex.learninghub.modules.review.dto.response.ReviewResponse;
import com.ex.learninghub.modules.review.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Review Module", description = "Endpoints for course reviews and ratings")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Submit a course review")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReviewResponse response = reviewService.createReview(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ReviewResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Review submitted successfully")
                        .result(response)
                        .build()
        );
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all reviews for a course")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByCourse(@PathVariable Long courseId) {
        List<ReviewResponse> response = reviewService.getReviewsByCourse(courseId);
        return ResponseEntity.ok(
                ApiResponse.<List<ReviewResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Reviews retrieved successfully")
                        .result(response)
                        .build()
        );
    }
}
