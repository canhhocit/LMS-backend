package com.ex.learninghub.modules.mentor.dto;

import com.ex.learninghub.common.enums.MentorRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for returning mentor request information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorRequestDTO {

    private Long id;
    private Long userId;
    private String userEmail;
    private String userFullName;
    private String bio;
    private String experience;
    private String skills;
    private MentorRequestStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
