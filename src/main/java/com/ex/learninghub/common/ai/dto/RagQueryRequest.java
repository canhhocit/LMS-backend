package com.ex.learninghub.common.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu hỏi đáp RAG theo tài liệu môn học")
public class RagQueryRequest {

    @Schema(description = "ID lớp học phần", example = "10")
    private Long clazzId;

    @Schema(description = "ID bài học (nếu muốn thu hẹp phạm vi trong 1 bài cụ thể)", example = "20")
    private Long lessonId;

    @Schema(description = "Câu hỏi của sinh viên", example = "Khái niệm Tính Đóng gói (Encapsulation) trong Java là gì?")
    @NotBlank(message = "Câu hỏi không được để trống")
    private String question;

    @Schema(description = "Số lượng đoạn văn bản trích xuất tốt nhất từ Vector Store", example = "3")
    @Builder.Default
    private Integer topK = 3;
}
