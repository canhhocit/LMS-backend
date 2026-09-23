package com.ex.learninghub.common.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin tùy chọn cá nhân hóa AI của người dùng")
public class UserAiPreferenceResponse {

    @Schema(description = "ID người dùng")
    private Long userId;

    @Schema(description = "Tên xưng hô mong muốn")
    private String preferredCallName;

    @Schema(description = "Giọng điệu AI")
    private String aiTone;

    @Schema(description = "Mô tả giọng điệu tùy chỉnh")
    private String customToneDescription;

    @Schema(description = "Độ dài / chi tiết phản hồi")
    private String responseLength;

    @Schema(description = "Ngữ cảnh cá nhân bổ sung")
    private String customContext;

    @Schema(description = "Thời gian cập nhật")
    private LocalDateTime updatedAt;
}
