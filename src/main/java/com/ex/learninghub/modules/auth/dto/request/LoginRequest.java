package com.ex.learninghub.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Username/Email cannot be blank")
    private String identifier; // Can be email, student_code, or lecturer_code

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
