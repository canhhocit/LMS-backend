package com.ex.learninghub.modules.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorApprovalRequest {
    @NotBlank(message = "KEY_INVALID")
    private String status;

    private String rejectionReason;
}
