package com.ex.learninghub.modules.registration.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.registration.dto.request.RegistrationPeriodRequest;
import com.ex.learninghub.modules.registration.dto.response.RegistrationPeriodResponse;
import com.ex.learninghub.modules.registration.dto.response.RegistrationResponse;
import com.ex.learninghub.modules.registration.service.RegistrationService;
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
public class RegistrationController {

    private final RegistrationService registrationService;

    // =================== Period management (ADMIN) ===================

    @PostMapping("/admin/registration-periods")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RegistrationPeriodResponse>> createPeriod(
            @Valid @RequestBody RegistrationPeriodRequest request) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.createPeriod(request)));
    }

    @PutMapping("/admin/registration-periods/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RegistrationPeriodResponse>> updatePeriod(
            @PathVariable Long id,
            @Valid @RequestBody RegistrationPeriodRequest request) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.updatePeriod(id, request)));
    }

    @DeleteMapping("/admin/registration-periods/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePeriod(@PathVariable Long id) {
        registrationService.deletePeriod(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/admin/registration-periods")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<RegistrationPeriodResponse>>> listPeriods() {
        return ResponseEntity.ok(ApiResponse.success(registrationService.listPeriods()));
    }

    @GetMapping("/registration-periods/active")
    public ResponseEntity<ApiResponse<RegistrationPeriodResponse>> getActivePeriod() {
        return ResponseEntity.ok(ApiResponse.success(registrationService.getActivePeriod()));
    }

    // =================== Student self-registration ===================

    @PostMapping("/registration/{clazzId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<RegistrationResponse>> register(
            @PathVariable Long clazzId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.register(clazzId, principal)));
    }

    @DeleteMapping("/registration/{clazzId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> unregister(
            @PathVariable Long clazzId,
            @AuthenticationPrincipal UserPrincipal principal) {
        registrationService.unregister(clazzId, principal);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/me/registrations")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<RegistrationResponse>>> getMyRegistrations(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.getMyRegistrations(principal)));
    }
}
