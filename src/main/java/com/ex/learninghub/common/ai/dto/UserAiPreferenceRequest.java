package com.ex.learninghub.common.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request cập nhật tùy chọn cá nhân hóa AI của người dùng")
public class UserAiPreferenceRequest {

    @Schema(description = "Cách xưng hô hoặc tên gọi mong muốn AI gọi mình (Ví dụ: 'Gọi tôi là Nguyên', 'Xưng em - gọi Thầy/Cô')", example = "Xưng em - gọi Thầy Cố vấn")
    @Size(max = 100, message = "Tên xưng hô tối đa 100 ký tự")
    private String preferredCallName;

    @Schema(description = "Giọng điệu AI (Ví dụ: FRIENDLY, STRICT, CONCISE, HUMOROUS, ACADEMIC, CUSTOM)", example = "FRIENDLY")
    @Size(max = 100, message = "Giọng điệu tối đa 100 ký tự")
    private String aiTone;

    @Schema(description = "Mô tả thêm giọng điệu cá nhân nếu cần", example = "Thân thiện nhưng thẳng thắn vào trọng tâm bài học")
    private String customToneDescription;

    @Schema(description = "Độ dài & mức độ chi tiết phản hồi (Ví dụ: CONCISE, DETAILED, STEP_BY_STEP)", example = "STEP_BY_STEP")
    @Size(max = 50, message = "Độ dài phản hồi tối đa 50 ký tự")
    private String responseLength;

    @Schema(description = "Ngữ cảnh cá nhân bổ sung do người dùng cung cấp", example = "Tôi đang làm Đồ án tốt nghiệp Java Spring Boot + React, ưu tiên giải thích code có comment tiếng Việt.")
    private String customContext;
}
