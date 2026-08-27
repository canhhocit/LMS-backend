package com.ex.learninghub.modules.tuition.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.tuition.dto.request.TuitionRateRequest;
import com.ex.learninghub.modules.tuition.dto.response.TuitionInvoiceResponse;
import com.ex.learninghub.modules.tuition.dto.response.TuitionRateResponse;
import com.ex.learninghub.modules.tuition.service.TuitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Học phí", description = "Các API xem hóa đơn học phí của sinh viên và quản lý mức học phí (Admin)")
public class TuitionController {

    private final TuitionService tuitionService;

    // ---- Student: xem hóa đơn của tôi ----
    @GetMapping("/me/tuition")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Sinh viên xem hóa đơn học phí của tôi",
            description = "Trả về danh sách các hóa đơn học phí của sinh viên hiện tại."
    )
    public ResponseEntity<ApiResponse<List<TuitionInvoiceResponse>>> getMyInvoices(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.getMyInvoices(principal)));
    }

    // ---- Admin: quản lý tuition_rates ----
    @PostMapping("/admin/tuition/rates")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Tạo mức học phí mới",
            description = "Admin tạo mới một mức học phí (ví dụ: theo tín chỉ, theo khối ngành, ...)."
    )
    public ResponseEntity<ApiResponse<TuitionRateResponse>> createRate(@Valid @RequestBody TuitionRateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.createRate(req)));
    }

    @PutMapping("/admin/tuition/rates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Cập nhật mức học phí",
            description = "Admin cập nhật một mức học phí đã có."
    )
    public ResponseEntity<ApiResponse<TuitionRateResponse>> updateRate(
            @Parameter(description = "ID của mức học phí", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TuitionRateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.updateRate(id, req)));
    }

    @DeleteMapping("/admin/tuition/rates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Xóa mức học phí",
            description = "Admin xóa một mức học phí khỏi hệ thống."
    )
    public ResponseEntity<ApiResponse<Void>> deleteRate(
            @Parameter(description = "ID của mức học phí cần xóa", example = "1")
            @PathVariable Long id) {
        tuitionService.deleteRate(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/admin/tuition/rates")
    @PreAuthorize("hasAnyRole('ADMIN','LECTURER')")
    @Operation(
            summary = "Lấy danh sách mức học phí",
            description = "Trả về danh sách tất cả mức học phí đang được áp dụng."
    )
    public ResponseEntity<ApiResponse<List<TuitionRateResponse>>> listRates() {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.listRates()));
    }

    // ---- Admin: generate / mark paid ----
    @PostMapping("/admin/tuition/{studentId}/generate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Sinh hóa đơn học phí cho sinh viên",
            description = "Admin tạo hóa đơn học phí cho một sinh viên theo học kỳ và năm học cụ thể."
    )
    public ResponseEntity<ApiResponse<TuitionInvoiceResponse>> generateInvoice(
            @Parameter(description = "ID của sinh viên", example = "1")
            @PathVariable Long studentId,
            @Parameter(description = "Học kỳ", example = "1")
            @RequestParam String semester,
            @Parameter(description = "Năm học", example = "2024-2025")
            @RequestParam String academicYear) {
        return ResponseEntity.ok(ApiResponse.success(
                tuitionService.generateInvoice(studentId, semester, academicYear)));
    }

    @PostMapping("/admin/tuition/{invoiceId}/mark-paid")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Đánh dấu hóa đơn đã thanh toán",
            description = "Admin cập nhật trạng thái đã thanh toán cho một hóa đơn học phí."
    )
    public ResponseEntity<ApiResponse<TuitionInvoiceResponse>> markPaid(
            @Parameter(description = "ID của hóa đơn", example = "1")
            @PathVariable Long invoiceId) {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.markPaid(invoiceId)));
    }
}
