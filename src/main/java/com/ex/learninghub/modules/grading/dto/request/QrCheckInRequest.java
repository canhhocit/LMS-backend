package com.ex.learninghub.modules.grading.dto.request;

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
public class QrCheckInRequest {

    @NotBlank(message = "Session token không được để trống")
    private String sessionToken;

    @NotBlank(message = "Mã OTP không được để trống")
    private String otpCode;

    private Double latitude;
    private Double longitude;
}
