package com.ex.learninghub.modules.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageSendRequest {
    @NotNull(message = "KEY_INVALID")
    private Long receiverId;

    @NotBlank(message = "KEY_INVALID")
    private String content;
}
