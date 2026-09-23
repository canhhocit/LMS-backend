package com.ex.learninghub.common.ai.controller;

import com.ex.learninghub.common.ai.dto.RagIngestRequest;
import com.ex.learninghub.common.ai.dto.RagQueryRequest;
import com.ex.learninghub.common.ai.dto.RagQueryResponse;
import com.ex.learninghub.common.ai.service.RagService;
import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai/rag")
@RequiredArgsConstructor
@Tag(name = "Spring AI RAG & Vector Store", description = "API nạp tài liệu và hỏi đáp RAG dựa trên tài liệu bài giảng môn học")
@SecurityRequirement(name = "bearerAuth")
public class RagController {

    private final RagService ragService;

    @PostMapping("/ingest")
    @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
    @Operation(summary = "Nạp tài liệu / slide bài giảng vào Vector Store (Dành cho Giảng viên)")
    public ResponseEntity<ApiResponse<Void>> ingestDocument(
            @Valid @RequestBody RagIngestRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ragService.ingestDocument(request, principal);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/query")
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'ADMIN')")
    @Operation(summary = "Hỏi đáp RAG thông minh dựa CHÍNH XÁC trên tài liệu môn học (Dành cho Sinh viên)")
    public ResponseEntity<ApiResponse<RagQueryResponse>> queryCourseMaterials(
            @Valid @RequestBody RagQueryRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        RagQueryResponse response = ragService.queryCourseMaterials(request, principal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
