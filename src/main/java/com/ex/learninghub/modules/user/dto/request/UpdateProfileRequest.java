package com.ex.learninghub.modules.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateProfileRequest {

    @NotBlank(message = "Full name cannot be blank")
    private String fullName;

    private LocalDate dateOfBirth;

    private String faculty;

    private String major;
}
