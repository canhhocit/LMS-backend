package com.ex.learninghub.modules.review.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long courseId;
    private Long learnerId;
    private String learnerName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
