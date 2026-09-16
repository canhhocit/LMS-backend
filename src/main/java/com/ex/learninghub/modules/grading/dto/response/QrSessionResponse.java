package com.ex.learninghub.modules.grading.dto.response;

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
public class QrSessionResponse {
    private Long classId;
    private String sessionToken;
    private String otpCode;
    private int expiresInSeconds;
    private String qrImageUrl;
}
