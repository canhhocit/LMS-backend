package com.ex.learninghub.modules.tuition.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.tuition.dto.request.TuitionRateRequest;
import com.ex.learninghub.modules.tuition.dto.response.TuitionInvoiceResponse;
import com.ex.learninghub.modules.tuition.dto.response.TuitionRateResponse;
import com.ex.learninghub.modules.tuition.service.TuitionService;
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
public class TuitionController {

    private final TuitionService tuitionService;

    // ---- Student: xem hóa đơn của tôi ----
    @GetMapping("/me/tuition")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<TuitionInvoiceResponse>>> getMyInvoices(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.getMyInvoices(principal)));
    }

    // ---- Admin: quản lý tuition_rates ----
    @PostMapping("/admin/tuition/rates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TuitionRateResponse>> createRate(@Valid @RequestBody TuitionRateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.createRate(req)));
    }

    @PutMapping("/admin/tuition/rates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TuitionRateResponse>> updateRate(@PathVariable Long id,
                                                                       @Valid @RequestBody TuitionRateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.updateRate(id, req)));
    }

    @DeleteMapping("/admin/tuition/rates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRate(@PathVariable Long id) {
        tuitionService.deleteRate(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/admin/tuition/rates")
    @PreAuthorize("hasAnyRole('ADMIN','LECTURER')")
    public ResponseEntity<ApiResponse<List<TuitionRateResponse>>> listRates() {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.listRates()));
    }

    // ---- Admin: generate / mark paid ----
    @PostMapping("/admin/tuition/{studentId}/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TuitionInvoiceResponse>> generateInvoice(
            @PathVariable Long studentId,
            @RequestParam String semester,
            @RequestParam String academicYear) {
        return ResponseEntity.ok(ApiResponse.success(
                tuitionService.generateInvoice(studentId, semester, academicYear)));
    }

    @PostMapping("/admin/tuition/{invoiceId}/mark-paid")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TuitionInvoiceResponse>> markPaid(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(ApiResponse.success(tuitionService.markPaid(invoiceId)));
    }
}
