package com.ex.learninghub.modules.user.dto.request;

import com.ex.learninghub.common.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class UserCreateRequest {

    @NotBlank(message = "Full name cannot be blank")
    private String fullName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Role is required")
    private Role role;

    private String studentCode;
    private String lecturerCode;
    private LocalDate dateOfBirth;
    private String faculty;
    private String major;
    private Long adminClassId;
}
