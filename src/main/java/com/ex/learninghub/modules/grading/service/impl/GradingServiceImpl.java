package com.ex.learninghub.modules.grading.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.grading.dto.request.AttendanceRecordItem;
import com.ex.learninghub.modules.grading.dto.request.AttendanceRequest;
import com.ex.learninghub.modules.grading.dto.request.GradeRequest;
import com.ex.learninghub.modules.grading.dto.response.AttendanceResponse;
import com.ex.learninghub.modules.grading.dto.response.GradeResponse;
import com.ex.learninghub.modules.grading.dto.response.TranscriptResponse;
import com.ex.learninghub.modules.grading.entity.Attendance;
import com.ex.learninghub.modules.grading.entity.Grade;
import com.ex.learninghub.modules.grading.repository.AttendanceRepository;
import com.ex.learninghub.modules.grading.repository.GradeRepository;
import com.ex.learninghub.modules.grading.service.GradingService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import com.ex.learninghub.common.enums.AttendanceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradingServiceImpl implements GradingService {

    private final GradeRepository gradeRepository;
    private final com.ex.learninghub.modules.notification.service.NotificationService notificationService;
    private final AttendanceRepository attendanceRepository;
    private final ClazzRepository clazzRepository;
    private final UserRepository userRepository;
    private final com.ex.learninghub.modules.grading.repository.GradingPolicyRepository gradingPolicyRepository;

    @Value("${app.attendance.max-absent-ratio:0.2}")
    private double maxAbsentRatio;

    private void verifyLecturerOwnsClazz(Clazz clazz, UserPrincipal userPrincipal) {
        if (userPrincipal.getUser().getRole() == com.ex.learninghub.common.enums.Role.ADMIN) {
            return;
        }
        if (clazz.getLecturer() == null ||
                !clazz.getLecturer().getId().equals(userPrincipal.getUser().getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    @Override
    @Transactional
    public GradeResponse upsertGrade(Long classId, GradeRequest request, UserPrincipal userPrincipal) {
        Clazz clazz = clazzRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, userPrincipal);
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Check attendance constraint
        long totalAttendance = attendanceRepository.countByClazzIdAndStudentId(classId, request.getStudentId());
        if (totalAttendance > 0) {
            long absentCount = attendanceRepository.countByClazzIdAndStudentIdAndStatus(classId, request.getStudentId(), AttendanceStatus.ABSENT);
            double absentRatio = (double) absentCount / totalAttendance;
            if (absentRatio > maxAbsentRatio) {
                throw new AppException(ErrorCode.ATTENDANCE_NOT_QUALIFIED);
            }
        }

        Grade grade = gradeRepository.findByClazzIdAndStudentId(classId, request.getStudentId())
                .orElse(Grade.builder().clazz(clazz).student(student).build());

        grade.setMidtermScore(request.getMidtermScore());
        grade.setFinalScore(request.getFinalScore());

        if (request.getMidtermScore() != null && request.getFinalScore() != null) {
            BigDecimal[] weights = resolveWeights(student);
            BigDecimal attWeight = weights[0];
            BigDecimal midWeight = weights[1];
            BigDecimal finWeight = weights[2];

            BigDecimal total;
            if (attWeight.compareTo(BigDecimal.ZERO) == 0) {
                total = request.getMidtermScore().multiply(midWeight)
                        .add(request.getFinalScore().multiply(finWeight))
                        .setScale(2, RoundingMode.HALF_UP);
            } else {
                BigDecimal attScore = resolveAttendanceScore10(classId, request.getStudentId());
                total = attScore.multiply(attWeight)
                        .add(request.getMidtermScore().multiply(midWeight))
                        .add(request.getFinalScore().multiply(finWeight))
                        .setScale(2, RoundingMode.HALF_UP);
            }
            grade.setTotalScore(total);
        } else {
            grade.setTotalScore(null);
        }

        Grade savedGrade = gradeRepository.save(grade);

        // Notify the student about their new grade (WebSocket + DB)
        notificationService.notifyUser(request.getStudentId(),
                com.ex.learninghub.common.enums.NotificationType.NEW_GRADE,
                "New grade posted",
                "Your grade for " + clazz.getClassName() + " has been updated",
                savedGrade.getId());

        return GradeResponse.from(savedGrade);
    }

    @Override
    public List<GradeResponse> getGradesByClass(Long classId, UserPrincipal userPrincipal) {
        Clazz clazz = clazzRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, userPrincipal);
        return gradeRepository.findByClazzId(classId).stream()
                .map(GradeResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<GradeResponse> getMyGrades(UserPrincipal userPrincipal) {
        return gradeRepository.findByStudentId(userPrincipal.getUser().getId()).stream()
                .map(GradeResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<AttendanceResponse> saveAttendance(Long classId, AttendanceRequest request, UserPrincipal userPrincipal) {
        Clazz clazz = clazzRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, userPrincipal);

        List<Attendance> saved = new ArrayList<>();
        if (request.getRecords() != null) {
            for (AttendanceRecordItem item : request.getRecords()) {
                User student = userRepository.findById(item.getStudentId())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
                Attendance attendance = attendanceRepository
                        .findByClazzIdAndStudentIdAndAttendanceDate(classId, item.getStudentId(), request.getAttendanceDate())
                        .orElse(Attendance.builder().clazz(clazz).student(student).attendanceDate(request.getAttendanceDate()).build());
                attendance.setStatus(item.getStatus());
                saved.add(attendanceRepository.save(attendance));
            }
        }
        return saved.stream().map(AttendanceResponse::from).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getAttendanceByDate(Long classId, LocalDate date, UserPrincipal userPrincipal) {
        Clazz clazz = clazzRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, userPrincipal);
        return attendanceRepository.findByClazzIdAndAttendanceDate(classId, date).stream()
                .map(AttendanceResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getMyAttendance(Long classId, UserPrincipal userPrincipal) {
        return attendanceRepository.findByClazzIdAndStudentId(classId, userPrincipal.getUser().getId()).stream()
                .map(AttendanceResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<TranscriptResponse> getMyTranscript(UserPrincipal userPrincipal) {
        Long studentId = userPrincipal.getUser().getId();
        List<Grade> grades = gradeRepository.findByStudentId(studentId);

        // Group by course and calculate GPA per course
        java.util.Map<Long, List<Grade>> gradesByCourse = new java.util.LinkedHashMap<>();
        for (Grade g : grades) {
            Long courseId = g.getClazz().getCourse().getId();
            gradesByCourse.computeIfAbsent(courseId, k -> new ArrayList<>()).add(g);
        }

        double totalWeightedScore = 0;
        int totalCredits = 0;
        List<TranscriptResponse> result = new ArrayList<>();

        for (var entry : gradesByCourse.entrySet()) {
            List<Grade> courseGrades = entry.getValue();
            // Chính sách học lại (retake): lấy điểm cao nhất trong các lần học.
            // Trước đây lấy courseGrades.get(size-1) = bản ghi cuối; đổi sang max.
            Grade bestGrade = courseGrades.stream()
                    .filter(g -> g.getTotalScore() != null)
                    .max(java.util.Comparator.comparing(Grade::getTotalScore))
                    .orElse(courseGrades.get(courseGrades.size() - 1));
            var course = bestGrade.getClazz().getCourse();
            int credit = course.getCredit() != null ? course.getCredit() : 0;
            BigDecimal totalScore = bestGrade.getTotalScore();

            double gpa = 0.0;
            if (totalScore != null && credit > 0) {
                gpa = totalScore.doubleValue();
                totalWeightedScore += gpa * credit;
                totalCredits += credit;
            }

            result.add(TranscriptResponse.builder()
                    .courseCode(course.getCode())
                    .courseTitle(course.getTitle())
                    .credit(credit)
                    .totalScore(totalScore)
                    .gpa(gpa)
                    .build());
        }

        // Set overall GPA on each entry
        double overallGpa = totalCredits > 0 ? totalWeightedScore / totalCredits : 0.0;
        for (TranscriptResponse t : result) {
            t.setGpa(Math.round(overallGpa * 100.0) / 100.0);
        }

        return result;
    }

    /**
     * Calc attendance score out of 10 based on attendance records.
     * attendanceScore10 = 10 * (presentCount + 0.5 * lateCount) / totalCount.
     * If totalCount == 0, returns 10.00.
     */
    private BigDecimal resolveAttendanceScore10(Long classId, Long studentId) {
        long totalCount = attendanceRepository.countByClazzIdAndStudentId(classId, studentId);
        if (totalCount == 0) {
            return new BigDecimal("10.00");
        }
        long presentCount = attendanceRepository.countByClazzIdAndStudentIdAndStatus(classId, studentId, AttendanceStatus.PRESENT);
        long lateCount = attendanceRepository.countByClazzIdAndStudentIdAndStatus(classId, studentId, AttendanceStatus.LATE);
        double score = 10.0 * (presentCount + 0.5 * lateCount) / totalCount;
        return BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal[] resolveWeights(User student) {
        if (student != null && student.getCurriculum() != null) {
            var policyOpt = gradingPolicyRepository.findByCurriculumId(student.getCurriculum().getId());
            if (policyOpt.isPresent()) {
                var p = policyOpt.get();
                return new BigDecimal[]{p.getAttendanceWeight(), p.getMidtermWeight(), p.getFinalWeight()};
            }
        }
        return new BigDecimal[]{new BigDecimal("0.000"), new BigDecimal("0.400"), new BigDecimal("0.600")};
    }
}
