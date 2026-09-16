package com.ex.learninghub.modules.grading.service.impl;

import com.ex.learninghub.common.config.AppProperties;
import com.ex.learninghub.common.enums.AttendanceStatus;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.grading.dto.response.AcademicRiskResponse;
import com.ex.learninghub.modules.grading.repository.AttendanceRepository;
import com.ex.learninghub.modules.grading.service.AcademicRiskWarningService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcademicRiskWarningServiceImpl implements AcademicRiskWarningService {

    private final ClazzRepository clazzRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final AppProperties appProperties;

    @Override
    public AcademicRiskResponse calculateStudentRisk(Long classId, Long studentId) {
        Clazz clazz = clazzRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        long totalSessions = attendanceRepository.countByClazzIdAndStudentId(classId, studentId);
        long absentCount = attendanceRepository.countByClazzIdAndStudentIdAndStatus(classId, studentId, AttendanceStatus.ABSENT);

        double absentRatio = totalSessions > 0 ? (double) absentCount / totalSessions : 0.0;
        double maxAllowedRatio = appProperties.getAttendance().getMaxAbsentRatio();

        double dummyAvgScore = 6.5; // Mock/calculated average score for current component grades

        double riskScore = (absentRatio * 0.6) + ((10.0 - dummyAvgScore) / 10.0 * 0.4);

        String riskLevel = "SAFE";
        String action = "Sinh viên đang duy trì tiến độ học tập tốt.";

        if (absentRatio >= maxAllowedRatio || riskScore >= 0.50) {
            riskLevel = "CRITICAL";
            action = "CẢNH BÁO NGUY CƠ CAO: Sinh viên có tỷ lệ vắng vượt ngưỡng/điểm số quá thấp. Cần gặp Cố vấn học tập gấp!";
        } else if (absentRatio >= maxAllowedRatio * 0.7 || riskScore >= 0.25) {
            riskLevel = "WARNING";
            action = "CẢNH BÁO NHẸ: Sinh viên cần chú ý tham gia lớp đầy đủ và cải thiện điểm bài tập.";
        }

        return AcademicRiskResponse.builder()
                .studentId(student.getId())
                .studentName(student.getFullName())
                .classId(clazz.getId())
                .className(clazz.getClassName())
                .absentCount(absentCount)
                .totalSessions(totalSessions)
                .absentRatio(Math.round(absentRatio * 100.0) / 100.0)
                .averageScore(dummyAvgScore)
                .riskLevel(riskLevel)
                .recommendationAction(action)
                .build();
    }

    @Override
    public List<AcademicRiskResponse> getClassRiskReport(Long classId) {
        Clazz clazz = clazzRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

        List<AcademicRiskResponse> reports = new ArrayList<>();
        // For demonstration, calculate for sample students or all enrolled
        List<User> students = userRepository.findAll();
        for (User student : students) {
            if (student.getId() != null) {
                reports.add(calculateStudentRisk(clazz.getId(), student.getId()));
                if (reports.size() >= 10) break;
            }
        }
        return reports;
    }
}
