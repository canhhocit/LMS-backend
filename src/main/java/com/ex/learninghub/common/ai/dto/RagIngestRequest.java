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
@Schema(description = "Yêu cầu nạp văn bản/bài giảng vào Vector Store để phục vụ RAG")
public class RagIngestRequest {

    @Schema(description = "ID bài học", example = "20")
    private Long lessonId;

    @Schema(description = "ID lớp học phần hoặc môn học", example = "10")
    private Long clazzId;

    @Schema(description = "Tên tài liệu / Tiêu đề slide bài giảng", example = "Slide Bài 1: Lập trình Hướng đối tượng")
    @NotBlank(message = "Tên nguồn tài liệu không được để trống")
    private String sourceName;

    @Schema(description = "Nội dung bài giảng / Văn bản cần phân đoạn và lưu Vector Embedding")
    @NotBlank(message = "Nội dung văn bản không được để trống")
    private String documentContent;
}
