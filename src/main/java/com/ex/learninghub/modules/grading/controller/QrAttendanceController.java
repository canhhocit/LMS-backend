package com.ex.learninghub.modules.grading.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.grading.dto.request.QrCheckInRequest;
import com.ex.learninghub.modules.grading.dto.response.AttendanceResponse;
import com.ex.learninghub.modules.grading.dto.response.QrSessionResponse;
import com.ex.learninghub.modules.grading.service.QrCodeAttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attendance/qr")
@RequiredArgsConstructor
@Tag(name = "Điểm danh QR Code động", description = "Các API tạo mã QR động OTP và thực hiện quét mã điểm danh tự động")
public class QrAttendanceController {

    private final QrCodeAttendanceService qrCodeAttendanceService;

    @PostMapping("/session/{classId}")
    @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
    @Operation(
            summary = "Giảng viên tạo mã QR động điểm danh",
            description = "Tạo phiên điểm danh QR code kèm mã OTP thay đổi liên tục mỗi 10 giây."
    )
    public ApiResponse<QrSessionResponse> generateQrSession(
            @PathVariable Long classId,
            @AuthenticationPrincipal UserPrincipal lecturerPrincipal) {
        return ApiResponse.success(qrCodeAttendanceService.generateQrSession(classId, lecturerPrincipal));
    }

    @PostMapping("/check-in")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(
            summary = "Sinh viên quét mã QR check-in điểm danh",
            description = "Xác thực OTP và vị trí để ghi nhận điểm danh có mặt (PRESENT) cho sinh viên."
    )
    public ApiResponse<AttendanceResponse> checkInWithQr(
            @Valid @RequestBody QrCheckInRequest request,
            @AuthenticationPrincipal UserPrincipal studentPrincipal) {
        return ApiResponse.success(qrCodeAttendanceService.checkInWithQr(request, studentPrincipal));
    }
}
