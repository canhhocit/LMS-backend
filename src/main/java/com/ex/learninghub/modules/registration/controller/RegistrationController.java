package com.ex.learninghub.modules.registration.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.registration.dto.request.RegistrationPeriodRequest;
import com.ex.learninghub.modules.registration.dto.response.RegistrationPeriodResponse;
import com.ex.learninghub.modules.registration.dto.response.RegistrationResponse;
import com.ex.learninghub.modules.registration.service.RegistrationService;
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
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Đăng ký học phần", description = "Các API quản lý đợt đăng ký và sinh viên đăng ký/hủy lớp học phần")
public class RegistrationController {

    private final RegistrationService registrationService;

    // =================== Period management (ADMIN) ===================

    @PostMapping("/admin/registration-periods")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Tạo đợt đăng ký học phần",
            description = "Admin tạo mới một đợt đăng ký học phần với thời gian mở/đóng và các ràng buộc."
    )
    public ResponseEntity<ApiResponse<RegistrationPeriodResponse>> createPeriod(
            @Valid @RequestBody RegistrationPeriodRequest request) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.createPeriod(request)));
    }

    @PutMapping("/admin/registration-periods/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Cập nhật đợt đăng ký",
            description = "Admin cập nhật thông tin (thời gian, ...) của một đợt đăng ký học phần."
    )
    public ResponseEntity<ApiResponse<RegistrationPeriodResponse>> updatePeriod(
            @Parameter(description = "ID của đợt đăng ký", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody RegistrationPeriodRequest request) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.updatePeriod(id, request)));
    }

    @DeleteMapping("/admin/registration-periods/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Xóa đợt đăng ký",
            description = "Admin xóa một đợt đăng ký học phần."
    )
    public ResponseEntity<ApiResponse<Void>> deletePeriod(
            @Parameter(description = "ID của đợt đăng ký cần xóa", example = "1")
            @PathVariable Long id) {
        registrationService.deletePeriod(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/admin/registration-periods")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Lấy danh sách đợt đăng ký",
            description = "Trả về danh sách tất cả các đợt đăng ký học phần."
    )
    public ResponseEntity<ApiResponse<List<RegistrationPeriodResponse>>> listPeriods() {
        return ResponseEntity.ok(ApiResponse.success(registrationService.listPeriods()));
    }

    @GetMapping("/registration-periods/active")
    @Operation(
            summary = "Lấy đợt đăng ký đang hoạt động",
            description = "Trả về đợt đăng ký học phần hiện đang mở cho sinh viên đăng ký."
    )
    public ResponseEntity<ApiResponse<RegistrationPeriodResponse>> getActivePeriod() {
        return ResponseEntity.ok(ApiResponse.success(registrationService.getActivePeriod()));
    }

    // =================== Student self-registration ===================

    @PostMapping("/registration/{clazzId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Sinh viên đăng ký lớp học phần",
            description = "Sinh viên đăng ký vào một lớp học phần cụ thể trong đợt đăng ký hiện tại."
    )
    public ResponseEntity<ApiResponse<RegistrationResponse>> register(
            @Parameter(description = "ID của lớp học phần", example = "1")
            @PathVariable Long clazzId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.register(clazzId, principal)));
    }

    @DeleteMapping("/registration/{clazzId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Sinh viên hủy đăng ký lớp học phần",
            description = "Sinh viên hủy đăng ký một lớp học phần (trong thời gian cho phép)."
    )
    public ResponseEntity<ApiResponse<Void>> unregister(
            @Parameter(description = "ID của lớp học phần", example = "1")
            @PathVariable Long clazzId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        registrationService.unregister(clazzId, principal);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/me/registrations")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Lấy danh sách đăng ký của tôi",
            description = "Sinh viên xem các lớp học phần mà mình đã đăng ký."
    )
    public ResponseEntity<ApiResponse<List<RegistrationResponse>>> getMyRegistrations(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.getMyRegistrations(principal)));
    }
}
