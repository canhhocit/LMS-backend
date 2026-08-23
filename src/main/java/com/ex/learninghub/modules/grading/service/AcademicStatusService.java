package com.ex.learninghub.modules.grading.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.grading.dto.response.AcademicStatusResponse;

public interface AcademicStatusService {
    /**
     * Lấy tình trạng học vụ của sinh viên hiện tại: GPA tích lũy, số tín chỉ đạt/trượt, mức cảnh báo.
     */
    AcademicStatusResponse getMyAcademicStatus(UserPrincipal principal);

    /**
     * Phiên bản nội bộ: lấy tình trạng học vụ của một sinh viên bất kỳ (dùng cho service khác,
     * ví dụ RegistrationService áp trần tín chỉ theo probation).
     */
    AcademicStatusResponse getMyAcademicStatusRaw(Long studentId);

    /**
     * Quét tất cả sinh viên có GPA dưới ngưỡng và phát cảnh báo qua Notification.
     * Gọi bởi cron / admin thủ công.
     */
    int scanAndWarnAcademicProbation();
}
