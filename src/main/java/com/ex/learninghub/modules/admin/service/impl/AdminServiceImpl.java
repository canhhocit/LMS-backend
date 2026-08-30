package com.ex.learninghub.modules.admin.service.impl;

import com.ex.learninghub.modules.admin.service.AdminService;
import com.ex.learninghub.modules.assessment.repository.AssignmentRepository;
import com.ex.learninghub.modules.assessment.repository.SubmissionRepository;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.enrollment.entity.Enrollment;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.grading.entity.Grade;
import com.ex.learninghub.modules.grading.repository.GradeRepository;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ClazzRepository clazzRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final GradeRepository gradeRepository;

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total users by role
        long totalStudents = userRepository.countByRole(com.ex.learninghub.common.enums.Role.STUDENT);
        long totalLecturers = userRepository.countByRole(com.ex.learninghub.common.enums.Role.LECTURER);
        long totalAdmins = userRepository.countByRole(com.ex.learninghub.common.enums.Role.ADMIN);

        Map<String, Long> usersByRole = new HashMap<>();
        usersByRole.put("STUDENT", totalStudents);
        usersByRole.put("LECTURER", totalLecturers);
        usersByRole.put("ADMIN", totalAdmins);

        // Total counts
        long totalClasses = clazzRepository.count();
        long totalEnrollments = enrollmentRepository.count();
        long totalAssignments = assignmentRepository.count();
        long totalSubmissions = submissionRepository.count();

        stats.put("totalUsers", totalStudents + totalLecturers + totalAdmins);
        stats.put("usersByRole", usersByRole);
        stats.put("totalClasses", totalClasses);
        stats.put("totalEnrollments", totalEnrollments);
        stats.put("totalAssignments", totalAssignments);
        stats.put("totalSubmissions", totalSubmissions);
        return stats;
    }

    @Override
    public Map<String, Object> getEnrollmentsByMonth() {
        DateTimeFormatter monthKey = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Long> byMonth = new TreeMap<>();
        for (Enrollment e : enrollmentRepository.findAll()) {
            if (e.getEnrolledAt() == null) continue;
            String key = e.getEnrolledAt().format(monthKey);
            byMonth.merge(key, 1L, Long::sum);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("series", byMonth);
        return result;
    }

    @Override
    public Map<String, Double> getAverageScoreByClazz() {
        Map<String, Double> averages = new LinkedHashMap<>();
        List<Clazz> clazzes = clazzRepository.findAll();
        for (Clazz clazz : clazzes) {
            List<Grade> grades = gradeRepository.findByClazzId(clazz.getId());
            double avg = grades.stream()
                    .map(Grade::getTotalScore)
                    .filter(s -> s != null)
                    .mapToDouble(java.math.BigDecimal::doubleValue)
                    .average()
                    .orElse(0.0);
            averages.put(clazz.getClassName(), Math.round(avg * 100.0) / 100.0);
        }
        return averages;
    }
}