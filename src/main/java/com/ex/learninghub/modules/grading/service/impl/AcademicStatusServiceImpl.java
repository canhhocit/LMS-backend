package com.ex.learninghub.modules.grading.service.impl;

import com.ex.learninghub.common.enums.NotificationType;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.grading.dto.response.AcademicStatusResponse;
import com.ex.learninghub.modules.grading.entity.Grade;
import com.ex.learninghub.modules.grading.repository.GradeRepository;
import com.ex.learninghub.modules.grading.service.AcademicStatusService;
import com.ex.learninghub.modules.notification.service.NotificationService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicStatusServiceImpl implements AcademicStatusService {

    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Value("${app.academic.pass-score:5.0}")
    private BigDecimal passScore;

    /** GPA dưới ngưỡng warning → cảnh báo */
    @Value("${app.academic.warning-gpa:2.0}")
    private BigDecimal warningGpa;

    /** GPA dưới ngưỡng probation (nặng) */
    @Value("${app.academic.probation-gpa:1.5}")
    private BigDecimal probationGpa;

    @Override
    @Transactional(readOnly = true)
    public AcademicStatusResponse getMyAcademicStatus(UserPrincipal principal) {
        return computeForStudent(principal.getUser().getId());
    }

    @Override
    @Transactional
    public int scanAndWarnAcademicProbation() {
        List<User> students = userRepository.findByRole(Role.STUDENT);
        int warned = 0;
        for (User s : students) {
            AcademicStatusResponse status = computeForStudent(s.getId());
            if (Boolean.TRUE.equals(status.getAcademicWarning())) {
                String title = "Cảnh báo học vụ";
                String content = String.format(
                        "GPA tích lũy: %.2f. Số môn đạt: %d/%d. Vui lòng liên hệ cố vấn học tập.",
                        status.getCumulativeGpa(),
                        status.getPassedCourses(),
                        status.getTotalCourses());
                notificationService.notifyUser(
                        s.getId(), NotificationType.ACADEMIC_WARNING, title, content, null);
                warned++;
            }
        }
        return warned;
    }

    private AcademicStatusResponse computeForStudent(Long studentId) {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);

        int totalCredits = 0;
        int passedCredits = 0;
        int totalCourses = grades.size();
        int passedCourses = 0;
        double weightedSum = 0.0;
        int weightedCount = 0;

        List<AcademicStatusResponse.FailedCourse> failed = new ArrayList<>();

        for (Grade g : grades) {
            Course c = g.getClazz() != null ? g.getClazz().getCourse() : null;
            Integer credits = c != null && c.getCredit() != null ? c.getCredit() : 0;
            totalCredits += credits;
            BigDecimal score = g.getTotalScore();

            if (score != null && score.compareTo(passScore) >= 0) {
                passedCourses++;
                passedCredits += credits;
                // GPA theo thang 4
                BigDecimal gpa4 = scoreToGpa4(score);
                weightedSum += gpa4.doubleValue() * credits;
                weightedCount += credits;
            } else if (score != null) {
                failed.add(AcademicStatusResponse.FailedCourse.builder()
                        .courseCode(c != null ? c.getCode() : null)
                        .courseTitle(c != null ? c.getTitle() : null)
                        .credit(credits)
                        .totalScore(score)
                        .build());
            }
        }

        BigDecimal cumulativeGpa = weightedCount > 0
                ? BigDecimal.valueOf(weightedSum / weightedCount).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        Integer warningLevel = 0;
        boolean warn = false;
        if (weightedCount > 0 && cumulativeGpa.compareTo(probationGpa) < 0) {
            warningLevel = 3;
            warn = true;
        } else if (weightedCount > 0 && cumulativeGpa.compareTo(warningGpa) < 0) {
            warningLevel = 1;
            warn = true;
        }

        return AcademicStatusResponse.builder()
                .cumulativeGpa(cumulativeGpa)
                .totalCredits(totalCredits)
                .passedCredits(passedCredits)
                .academicWarning(warn)
                .warningLevel(warningLevel)
                .totalCourses(totalCourses)
                .passedCourses(passedCourses)
                .failedCourses(failed)
                .build();
    }

    /**
     * Quy đổi điểm hệ 10 sang thang 4 chuẩn Việt Nam.
     */
    private static BigDecimal scoreToGpa4(BigDecimal score10) {
        double s = score10.doubleValue();
        double g;
        if (s >= 9.0) g = 4.0;
        else if (s >= 8.5) g = 3.7;
        else if (s >= 8.0) g = 3.5;
        else if (s >= 7.0) g = 3.0;
        else if (s >= 6.5) g = 2.5;
        else if (s >= 5.5) g = 2.0;
        else if (s >= 5.0) g = 1.5;
        else if (s >= 4.0) g = 1.0;
        else g = 0.0;
        return BigDecimal.valueOf(g);
    }
}
