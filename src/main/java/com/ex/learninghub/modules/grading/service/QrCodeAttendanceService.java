package com.ex.learninghub.modules.grading.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.grading.dto.request.QrCheckInRequest;
import com.ex.learninghub.modules.grading.dto.response.AttendanceResponse;
import com.ex.learninghub.modules.grading.dto.response.QrSessionResponse;

public interface QrCodeAttendanceService {

    /**
     * Giảng viên tạo/lấy phiên điểm danh QR động (đổi OTP mỗi 10 giây)
     */
    QrSessionResponse generateQrSession(Long classId, UserPrincipal lecturerPrincipal);

    /**
     * Sinh viên quét mã QR check-in điểm danh kèm xác thực OTP & Geolocation
     */
    AttendanceResponse checkInWithQr(QrCheckInRequest request, UserPrincipal studentPrincipal);
}
