package com.ex.learninghub.modules.tuition.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.tuition.dto.request.TuitionRateRequest;
import com.ex.learninghub.modules.tuition.dto.response.PayOSPaymentResponse;
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
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Học phí", description = "Các API xem hóa đơn học phí của sinh viên, thanh toán PayOS và quản lý mức học phí (Admin)")
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

    @PostMapping("/me/tuition/{invoiceId}/pay")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Sinh viên thanh toán hóa đơn học phí",
            description = "Thanh toán hóa đơn học phí trực tuyến."
    )
    public ResponseEntity<ApiResponse<TuitionInvoiceResponse>> payMyInvoice(
            @PathVariable Long invoiceId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.payMyInvoice(invoiceId, principal)));
    }

    // ---- PayOS Payment Integration ----
    @PostMapping("/me/tuition/{invoiceId}/payos-create-payment")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Sinh viên tạo thanh toán PayOS",
            description = "Sinh link thanh toán PayOS và mã VietQR cho hóa đơn học phí."
    )
    public ResponseEntity<ApiResponse<PayOSPaymentResponse>> createPayOSPayment(
            @PathVariable Long invoiceId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.createPayOSPayment(invoiceId, principal)));
    }

    @PostMapping("/me/tuition/{invoiceId}/payos-verify")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Sinh viên xác minh thanh toán PayOS",
            description = "Xác minh và chuyển trạng thái hóa đơn thành ĐÃ THANH TOÁN (PAID)."
    )
    public ResponseEntity<ApiResponse<TuitionInvoiceResponse>> verifyPayOSPayment(
            @PathVariable Long invoiceId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.verifyPayOSPayment(invoiceId, principal)));
    }

    @PostMapping("/public/payos-webhook")
    @Operation(
            summary = "Webhook PayOS callback tự động",
            description = "Nhận webhook thông báo từ PayOS khi thanh toán thành công để gạch nợ tự động."
    )
    public ResponseEntity<ApiResponse<TuitionInvoiceResponse>> processPayOSWebhook(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.processPayOSWebhook(payload)));
    }

    // ---- Admin: quản lý tuition_rates ----
    @PostMapping("/admin/tuition/rates")
    @PreAuthorize("hasPermission(null, 'MANAGE_TUITION')")
    @Operation(
            summary = "Tạo mức học phí mới",
            description = "Admin tạo mới một mức học phí."
    )
    public ResponseEntity<ApiResponse<TuitionRateResponse>> createRate(@Valid @RequestBody TuitionRateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.createRate(req)));
    }

    @PutMapping("/admin/tuition/rates/{id}")
    @PreAuthorize("hasPermission(null, 'MANAGE_TUITION')")
    @Operation(
            summary = "Cập nhật mức học phí",
            description = "Admin cập nhật một mức học phí đã có."
    )
    public ResponseEntity<ApiResponse<TuitionRateResponse>> updateRate(
            @PathVariable Long id,
            @Valid @RequestBody TuitionRateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.updateRate(id, req)));
    }

    @DeleteMapping("/admin/tuition/rates/{id}")
    @PreAuthorize("hasPermission(null, 'MANAGE_TUITION')")
    @Operation(
            summary = "Xóa mức học phí",
            description = "Admin xóa một mức học phí khỏi hệ thống."
    )
    public ResponseEntity<ApiResponse<Void>> deleteRate(@PathVariable Long id) {
        tuitionService.deleteRate(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/admin/tuition/rates")
    @PreAuthorize("hasAnyRole('ADMIN','LECTURER','STUDENT')")
    @Operation(
            summary = "Lấy danh sách mức học phí",
            description = "Trả về danh sách tất cả mức học phí đang được áp dụng."
    )
    public ResponseEntity<ApiResponse<List<TuitionRateResponse>>> listRates() {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.listRates()));
    }

    // ---- Admin: generate / mark paid ----
    @PostMapping("/admin/tuition/{studentId}/generate")
    @PreAuthorize("hasPermission(null, 'MANAGE_TUITION')")
    @Operation(
            summary = "Sinh hóa đơn học phí cho sinh viên",
            description = "Admin tạo hóa đơn học phí cho một sinh viên theo học kỳ và năm học."
    )
    public ResponseEntity<ApiResponse<TuitionInvoiceResponse>> generateInvoice(
            @PathVariable Long studentId,
            @RequestParam String semester,
            @RequestParam String academicYear) {
        return ResponseEntity.ok(ApiResponse.success(
                tuitionService.generateInvoice(studentId, semester, academicYear)));
    }

    @PostMapping("/admin/tuition/{invoiceId}/mark-paid")
    @PreAuthorize("hasPermission(null, 'MANAGE_TUITION')")
    @Operation(
            summary = "Đánh dấu hóa đơn đã thanh toán",
            description = "Admin cập nhật trạng thái đã thanh toán cho một hóa đơn học phí."
    )
    public ResponseEntity<ApiResponse<TuitionInvoiceResponse>> markPaid(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.markPaid(invoiceId)));
    }
}
