package com.ex.learninghub.modules.grading.service;

import com.ex.learninghub.modules.grading.dto.response.AcademicRiskResponse;

import java.util.List;

public interface AcademicRiskWarningService {

    /**
     * Tính toán nguy cơ rủi ro học tập của sinh viên trong một lớp học phần
     */
    AcademicRiskResponse calculateStudentRisk(Long classId, Long studentId);

    /**
     * Lấy danh sách cảnh báo rủi ro học tập của tất cả sinh viên trong lớp
     */
    List<AcademicRiskResponse> getClassRiskReport(Long classId);
}
