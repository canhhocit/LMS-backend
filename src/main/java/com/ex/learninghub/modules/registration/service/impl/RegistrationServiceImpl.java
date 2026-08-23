package com.ex.learninghub.modules.registration.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.entity.ClassSchedule;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.course.repository.ClassScheduleRepository;
import com.ex.learninghub.modules.curriculum.repository.CoursePrerequisiteRepository;
import com.ex.learninghub.modules.curriculum.entity.CoursePrerequisite;
import com.ex.learninghub.modules.enrollment.entity.Enrollment;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.enrollment.repository.LessonProgressRepository;
import com.ex.learninghub.modules.registration.dto.request.RegistrationPeriodRequest;
import com.ex.learninghub.modules.registration.dto.response.RegistrationPeriodResponse;
import com.ex.learninghub.modules.registration.dto.response.RegistrationResponse;
import com.ex.learninghub.modules.registration.entity.RegistrationPeriod;
import com.ex.learninghub.modules.registration.repository.RegistrationPeriodRepository;
import com.ex.learninghub.modules.registration.service.RegistrationService;
import com.ex.learninghub.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationPeriodRepository periodRepository;
    private final ClazzRepository clazzRepository;
    private final ClassScheduleRepository scheduleRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CoursePrerequisiteRepository prerequisiteRepository;
    private final com.ex.learninghub.modules.grading.repository.GradeRepository gradeRepository;
    private final com.ex.learninghub.modules.grading.service.AcademicStatusService academicStatusService;

    /** Trần tín chỉ áp dụng cho sinh viên bị probation (warningLevel >= 2). */
    @org.springframework.beans.factory.annotation.Value("${app.registration.max-credits-probation:14}")
    private int probationMaxCredits;

    // =================== PERIOD CRUD ===================

    @Override
    @Transactional
    public RegistrationPeriodResponse createPeriod(RegistrationPeriodRequest request) {
        validateWindow(request.getOpenAt(), request.getCloseAt());

        if (Boolean.TRUE.equals(request.getIsActive())) {
            deactivateAll();
        }

        RegistrationPeriod p = RegistrationPeriod.builder()
                .name(request.getName())
                .semester(request.getSemester())
                .academicYear(request.getAcademicYear())
                .openAt(request.getOpenAt())
                .closeAt(request.getCloseAt())
                .maxCredits(request.getMaxCredits())
                .isActive(Boolean.TRUE.equals(request.getIsActive()))
                .build();
        return RegistrationPeriodResponse.from(periodRepository.save(p));
    }

    @Override
    @Transactional
    public RegistrationPeriodResponse updatePeriod(Long id, RegistrationPeriodRequest request) {
        RegistrationPeriod p = periodRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REGISTRATION_CLOSED));
        validateWindow(request.getOpenAt(), request.getCloseAt());

        p.setName(request.getName());
        p.setSemester(request.getSemester());
        p.setAcademicYear(request.getAcademicYear());
        p.setOpenAt(request.getOpenAt());
        p.setCloseAt(request.getCloseAt());
        p.setMaxCredits(request.getMaxCredits());
        if (Boolean.TRUE.equals(request.getIsActive())) {
            deactivateAll();
            p.setIsActive(true);
        } else {
            p.setIsActive(false);
        }
        return RegistrationPeriodResponse.from(periodRepository.save(p));
    }

    @Override
    @Transactional
    public void deletePeriod(Long id) {
        if (!periodRepository.existsById(id)) {
            throw new AppException(ErrorCode.REGISTRATION_CLOSED);
        }
        periodRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationPeriodResponse> listPeriods() {
        return periodRepository.findAll().stream()
                .map(RegistrationPeriodResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationPeriodResponse getActivePeriod() {
        return periodRepository.findByIsActiveTrue()
                .map(RegistrationPeriodResponse::from)
                .orElseThrow(() -> new AppException(ErrorCode.REGISTRATION_CLOSED));
    }

    // =================== STUDENT OPERATIONS ===================

    @Override
    @Transactional
    public RegistrationResponse register(Long clazzId, UserPrincipal principal) {
        RegistrationPeriod period = getOpenPeriod();
        User student = principal.getUser();

        if (enrollmentRepository.existsByStudentIdAndClazzId(student.getId(), clazzId)) {
            throw new AppException(ErrorCode.ENROLLMENT_EXISTS);
        }

        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

        // Tín chỉ của lớp muốn đăng ký
        int addingCredits = clazz.getCourse() != null && clazz.getCourse().getCredit() != null
                ? clazz.getCourse().getCredit() : 0;
        if (period.getMaxCredits() != null) {
            int effectiveMax = resolveEffectiveMaxCredits(student.getId(), period.getMaxCredits());
            int currentCredits = computeCurrentCredits(student.getId(), clazzId);
            if (currentCredits + addingCredits > effectiveMax) {
                throw new AppException(ErrorCode.CREDIT_LIMIT_EXCEEDED);
            }
        }

        // Kiểm tra trùng lịch với các lớp đã đăng ký trong cùng kỳ
        checkScheduleConflict(student.getId(), clazzId);

        // Kiểm tra môn tiên quyết
        if (clazz.getCourse() != null) {
            List<CoursePrerequisite> prereqs = prerequisiteRepository.findByCourseId(clazz.getCourse().getId());
            if (!prereqs.isEmpty()) {
                List<Long> passed = gradeRepository.findPassedCourseIds(
                        student.getId(), new java.math.BigDecimal("5.0"));
                boolean ok = prereqs.stream().allMatch(p -> passed.contains(p.getPrerequisiteCourseId()));
                if (!ok) {
                    throw new AppException(ErrorCode.PREREQUISITE_NOT_MET);
                }
            }
        }

        Enrollment e = Enrollment.builder()
                .student(student)
                .clazz(clazz)
                .enrolledAt(LocalDateTime.now())
                .status("ACTIVE")
                .build();
        return RegistrationResponse.from(enrollmentRepository.save(e));
    }

    @Override
    @Transactional
    public void unregister(Long clazzId, UserPrincipal principal) {
        RegistrationPeriod period = getOpenPeriod();
        User student = principal.getUser();

        Enrollment e = enrollmentRepository.findByStudentIdAndClazzId(student.getId(), clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        // Xóa progress trước
        lessonProgressRepository.findByEnrollmentId(e.getId())
                .forEach(lessonProgressRepository::delete);
        enrollmentRepository.delete(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationResponse> getMyRegistrations(UserPrincipal principal) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(principal.getUser().getId());
        return enrollments.stream()
                .map(RegistrationResponse::from)
                .collect(Collectors.toList());
    }

    // =================== helpers ===================

    private RegistrationPeriod getOpenPeriod() {
        RegistrationPeriod p = periodRepository.findByIsActiveTrue()
                .orElseThrow(() -> new AppException(ErrorCode.REGISTRATION_CLOSED));
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(p.getOpenAt()) || now.isAfter(p.getCloseAt())) {
            throw new AppException(ErrorCode.REGISTRATION_CLOSED);
        }
        return p;
    }

    private static void validateWindow(LocalDateTime open, LocalDateTime close) {
        if (open == null || close == null || !close.isAfter(open)) {
            throw new AppException(ErrorCode.REGISTRATION_CLOSED);
        }
    }

    private void deactivateAll() {
        periodRepository.findAll().forEach(p -> {
            if (Boolean.TRUE.equals(p.getIsActive())) {
                p.setIsActive(false);
                periodRepository.save(p);
            }
        });
    }

    /**
     * Sinh viên bị cảnh báo học vụ nặng (warningLevel >= 2 = probation) sẽ bị áp dụng
     * trần tín chỉ riêng thấp hơn. Trả về giá trị nhỏ hơn giữa trần mặc định và trần probation.
     */
    private int resolveEffectiveMaxCredits(Long studentId, int defaultMax) {
        try {
            var status = academicStatusService.getMyAcademicStatusRaw(studentId);
            if (status != null && status.getWarningLevel() != null && status.getWarningLevel() >= 2) {
                return Math.min(defaultMax, probationMaxCredits);
            }
        } catch (Exception ignored) {
            // Nếu tính status lỗi (sinh viên chưa có điểm) thì dùng trần mặc định.
        }
        return defaultMax;
    }

    private int computeCurrentCredits(Long studentId, Long excludeClazzId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .filter(e -> !e.getClazz().getId().equals(excludeClazzId))
                .mapToInt(e -> e.getClazz() != null
                        && e.getClazz().getCourse() != null
                        && e.getClazz().getCourse().getCredit() != null
                        ? e.getClazz().getCourse().getCredit() : 0)
                .sum();
    }

    private void checkScheduleConflict(Long studentId, Long newClazzId) {
        List<Long> currentClazzIds = enrollmentRepository.findByStudentId(studentId).stream()
                .map(e -> e.getClazz().getId())
                .toList();

        List<ClassSchedule> newSchedules = scheduleRepository.findByClazzId(newClazzId);
        if (newSchedules.isEmpty() || currentClazzIds.isEmpty()) {
            return;
        }

        for (Long cid : currentClazzIds) {
            List<ClassSchedule> existing = scheduleRepository.findByClazzId(cid);
            for (ClassSchedule ns : newSchedules) {
                for (ClassSchedule es : existing) {
                    if (ns.getDayOfWeek().equals(es.getDayOfWeek())
                            && periodsOverlap(ns.getStartPeriod(), ns.getEndPeriod(),
                                    es.getStartPeriod(), es.getEndPeriod())) {
                        throw new AppException(ErrorCode.SCHEDULE_CONFLICT);
                    }
                }
            }
        }
    }

    private static boolean periodsOverlap(int s1, int e1, int s2, int e2) {
        return s1 <= e2 && s2 <= e1;
    }
}
