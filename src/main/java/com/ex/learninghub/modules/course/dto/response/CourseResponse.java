package com.ex.learninghub.modules.course.dto.response;

import java.math.BigDecimal;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Long mentorId;
    private String status;
}
