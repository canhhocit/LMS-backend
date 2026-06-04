package com.ex.learninghub.modules.admin.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorRequestResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private String bio;
    private String experience;
    private String skills;
    private String status;
    private String rejectionReason;
    private LocalDateTime createdAt;
}
