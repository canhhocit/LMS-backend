package com.ex.learninghub.modules.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "KEY_INVALID")
    private String email;

    @NotBlank(message = "KEY_INVALID")
    @Size(min = 6, message = "KEY_INVALID")
    private String password;

    @NotBlank(message = "KEY_INVALID")
    private String fullName;
}
