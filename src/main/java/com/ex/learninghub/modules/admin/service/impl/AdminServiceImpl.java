package com.ex.learninghub.modules.admin.service.impl;

import com.ex.learninghub.modules.admin.service.AdminService;
import com.ex.learninghub.modules.assessment.repository.AssignmentRepository;
import com.ex.learninghub.modules.assessment.repository.SubmissionRepository;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ClazzRepository clazzRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;

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

        stats.put("usersByRole", usersByRole);
        stats.put("totalClasses", totalClasses);
        stats.put("totalEnrollments", totalEnrollments);
        stats.put("totalAssignments", totalAssignments);
        stats.put("totalSubmissions", totalSubmissions);

        return stats;
    }
}