package com.ex.learninghub.common.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Kết quả phản hồi hỏi đáp RAG")
public class RagQueryResponse {

    @Schema(description = "Câu hỏi ban đầu")
    private String question;

    @Schema(description = "Câu trả lời do AI tổng hợp dựa trên tài liệu")
    private String answer;

    @Schema(description = "Danh sách các đoạn tài liệu tham khảo được trích xuất từ Vector Store")
    private List<String> retrievedSources;

    @Schema(description = "Đánh dấu câu trả lời có sử dụng dữ liệu Vector Store hay không")
    private Boolean isRagUsed;

    @Schema(description = "Số lượng đoạn văn ngữ cảnh trích xuất thành công")
    private Integer retrievedCount;
}
