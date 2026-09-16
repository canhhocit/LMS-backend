package com.ex.learninghub.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotBlank(message = "Old password cannot be blank")
    private String oldPassword;

    @NotBlank(message = "New password cannot be blank")
    @Pattern(
        regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,}$",
        message = "Mật khẩu mới phải có ít nhất 6 ký tự và chứa ít nhất 1 ký tự đặc biệt (VD: @, #, $, !...)"
    )
    private String newPassword;
}
