package com.ex.learninghub.modules.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageDTO {

    @NotNull(message = "Receiver ID is required")
    private Long receiverId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotBlank(message = "Content is required")
    private String content;
}