package com.ex.learninghub.modules.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @JsonAlias({"username", "email"})
    @NotBlank(message = "Username/Email cannot be blank")
    private String identifier; // Can be email, student_code, or lecturer_code

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
