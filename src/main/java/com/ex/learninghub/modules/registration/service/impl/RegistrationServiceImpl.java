package com.ex.learninghub.modules.registration.service.impl;

import com.ex.learninghub.common.enums.NotificationType;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.entity.ClassSchedule;
import com.ex.learninghub.modules.course.entity.Lesson;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.course.repository.ClassScheduleRepository;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import com.ex.learninghub.modules.curriculum.repository.CoursePrerequisiteRepository;
import com.ex.learninghub.modules.curriculum.entity.CoursePrerequisite;
import com.ex.learninghub.modules.enrollment.entity.Enrollment;
import com.ex.learninghub.modules.enrollment.entity.LessonProgress;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.enrollment.repository.LessonProgressRepository;
import com.ex.learninghub.modules.notification.service.NotificationService;
import com.ex.learninghub.modules.registration.dto.request.RegistrationPeriodRequest;
import com.ex.learninghub.modules.registration.dto.response.RegistrationPeriodResponse;
import com.ex.learninghub.modules.registration.dto.response.RegistrationResponse;
import com.ex.learninghub.modules.registration.entity.RegistrationPeriod;
import com.ex.learninghub.modules.registration.repository.RegistrationPeriodRepository;
import com.ex.learninghub.modules.registration.service.RegistrationService;
import com.ex.learninghub.modules.semester.entity.AcademicSemester;
import com.ex.learninghub.modules.semester.repository.AcademicSemesterRepository;
import com.ex.learninghub.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import com.ex.learninghub.modules.course.dto.response.ClazzResponse;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private static final ZoneId REGISTRATION_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final RegistrationPeriodRepository periodRepository;
    private final ClazzRepository clazzRepository;
    private final ClassScheduleRepository scheduleRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final LessonRepository lessonRepository;
    private final CoursePrerequisiteRepository prerequisiteRepository;
    private final com.ex.learninghub.modules.grading.repository.GradeRepository gradeRepository;
    private final com.ex.learninghub.modules.grading.service.AcademicStatusService academicStatusService;
    private final NotificationService notificationService;
    private final com.ex.learninghub.modules.tuition.service.TuitionService tuitionService;
    private final com.ex.learninghub.common.email.EmailService emailService;
    private final AcademicSemesterRepository semesterRepository;

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

        AcademicSemester sem = null;
        if (request.getSemesterId() != null) {
            sem = semesterRepository.findById(request.getSemesterId())
                    .orElseThrow(() -> new RuntimeException("Semester not found"));
        }

        RegistrationPeriod p = RegistrationPeriod.builder()
                .name(request.getName())
                .semester(request.getSemester())
                .academicYear(request.getAcademicYear())
                .academicSemester(sem)
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

        if (request.getSemesterId() != null) {
            AcademicSemester sem = semesterRepository.findById(request.getSemesterId())
                    .orElseThrow(() -> new RuntimeException("Semester not found"));
            p.setAcademicSemester(sem);
        }

        p.setName(request.getName());
        if (request.getSemester() != null) p.setSemester(request.getSemester());
        if (request.getAcademicYear() != null) p.setAcademicYear(request.getAcademicYear());
        p.setOpenAt(request.getOpenAt());
        p.setCloseAt(request.getCloseAt());
        p.setMaxCredits(request.getMaxCredits());
        if (Boolean.TRUE.equals(request.getIsActive())) {
            if (!LocalDateTime.now(REGISTRATION_ZONE).isBefore(p.getCloseAt())) {
                throw new AppException(ErrorCode.REGISTRATION_CLOSED);
            }
            deactivateAll();
            p.setIsActive(true);
        } else {
            p.setIsActive(false);
        }
        return RegistrationPeriodResponse.from(periodRepository.save(p));
    }

    @Override
    @Transactional
    public RegistrationPeriodResponse setPeriodActive(Long id, boolean active) {
        RegistrationPeriod period = periodRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REGISTRATION_CLOSED));

        if (active) {
            LocalDateTime now = LocalDateTime.now(REGISTRATION_ZONE);
            if (!now.isBefore(period.getCloseAt())) {
                throw new AppException(ErrorCode.REGISTRATION_CLOSED);
            }
            deactivateAll();
        }
        period.setIsActive(active);
        return RegistrationPeriodResponse.from(periodRepository.save(period));
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
                .filter(p -> isWithinWindow(LocalDateTime.now(REGISTRATION_ZONE), p.getOpenAt(), p.getCloseAt()))
                .map(RegistrationPeriodResponse::from)
                .orElse(null);
    }

    @org.springframework.scheduling.annotation.Scheduled(fixedDelay = 60000)
    @Transactional
    public void deactivateExpiredPeriods() {
        periodRepository.deactivateExpiredActive(LocalDateTime.now(REGISTRATION_ZONE));
    }

    // =================== PERIOD CLASSES ===================

    @Override
    @Transactional
    public void addClazzToPeriod(Long periodId, Long clazzId) {
        RegistrationPeriod period = periodRepository.findActiveWithClasses()
            .filter(p -> p.getId().equals(periodId))
            .orElseGet(() -> periodRepository.findById(periodId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)));

        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

        // Check if already enrolled in another class of the SAME course in this semester
        if (clazz.getCourse() != null) {
            boolean duplicateCourse = enrollmentRepository.findByStudentId(student.getId()).stream()
                    .anyMatch(e -> e.getClazz().getCourse() != null 
                            && e.getClazz().getCourse().getId().equals(clazz.getCourse().getId())
                            && e.getSemester().equals(period.getEffectiveSemester())
                            && e.getAcademicYear().equals(period.getEffectiveAcademicYear()));
            if (duplicateCourse) {
                throw new AppException(ErrorCode.ENROLLMENT_EXISTS);
            }
        }

        // Check if class is in the allowed classes for this period
        RegistrationPeriod activePeriodWithClasses = periodRepository.findActiveWithClasses().orElse(period);
        boolean isAllowed = activePeriodWithClasses.getAllowedClasses().stream()
                .anyMatch(c -> c.getId().equals(clazzId));
        if (!isAllowed && !activePeriodWithClasses.getAllowedClasses().isEmpty()) {
            throw new AppException(ErrorCode.FORBIDDEN); // Class is not in the allowed list for this period
        }

        if (period.getAcademicSemester() != null && clazz.getAcademicSemester() != null) {
            if (!period.getAcademicSemester().getId().equals(clazz.getAcademicSemester().getId())) {
                throw new AppException(ErrorCode.SEMESTER_MISMATCH);
            }
        }

        if (clazz.getMaxStudents() != null) {
            long current = enrollmentRepository.countByClazzId(clazzId);
            if (current + 1 > clazz.getMaxStudents()) {
                throw new AppException(ErrorCode.CLAZZ_FULL);
            }
        }

        int addingCredits = clazz.getCourse() != null && clazz.getCourse().getCredit() != null
                ? clazz.getCourse().getCredit() : 0;
        if (period.getMaxCredits() != null) {
            int effectiveMax = resolveEffectiveMaxCredits(student.getId(), period.getMaxCredits());
            int currentCredits = computeCurrentCredits(student.getId(), clazzId);
            if (currentCredits + addingCredits > effectiveMax) {
                throw new AppException(ErrorCode.CREDIT_LIMIT_EXCEEDED);
            }
        }

        checkScheduleConflict(student.getId(), clazzId);

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
                .semester(period.getEffectiveSemester())
                .academicYear(period.getEffectiveAcademicYear())
                .enrolledAt(LocalDateTime.now())
                .status("ACTIVE")
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(e);

        List<Lesson> clazzLessons = lessonRepository.findByClazzId(clazzId);
        if (!clazzLessons.isEmpty()) {
            List<LessonProgress> progressRecords = clazzLessons.stream()
                    .map(lesson -> LessonProgress.builder()
                            .enrollment(savedEnrollment)
                            .lesson(lesson)
                            .isCompleted(false)
                            .build())
                    .toList();
            lessonProgressRepository.saveAll(progressRecords);
        }
        
        notificationService.notifyUser(
            student.getId(),
            NotificationType.COURSE_REGISTERED,
            "Đã đăng ký lớp: " + clazz.getClassName(),
            "Bạn đã đăng ký thành công lớp " + clazz.getClassName(),
            clazz.getId()
        );
        
        if (clazz.getLecturer() != null) {
            notificationService.notifyUser(
                clazz.getLecturer().getId(),
                NotificationType.COURSE_REGISTERED,
                "Sinh viên mới: " + student.getFullName(),
                student.getFullName() + " vừa đăng ký lớp " + clazz.getClassName(),
                clazz.getId()
            );
        }
        
        return RegistrationResponse.from(savedEnrollment);
    }

    @Override
    @Transactional
    public RegistrationResponse register(Long clazzId, UserPrincipal principal) {
        RegistrationPeriod period = getOpenPeriod();
        RegistrationResponse resp = registerInternal(clazzId, principal, period);
        
        if (period.getEffectiveSemester() != null && period.getEffectiveAcademicYear() != null) {
            try {
                tuitionService.generateInvoice(principal.getUser().getId(), period.getEffectiveSemester(), period.getEffectiveAcademicYear());
            } catch (Exception ignored) {}
        }
        
        Clazz clazz = clazzRepository.findById(clazzId).orElse(null);
        if (clazz != null) {
            emailService.sendCourseRegistrationEmail(principal.getUser(), clazz, period);
        }
        return resp;
    }

    @Override
    @Transactional
    public List<RegistrationResponse> batchRegister(List<Long> clazzIds, UserPrincipal principal) {
        RegistrationPeriod period = getOpenPeriod();
        List<RegistrationResponse> responses = new java.util.ArrayList<>();
        
        for (Long clazzId : clazzIds) {
            RegistrationResponse resp = registerInternal(clazzId, principal, period);
            responses.add(resp);
            
            Clazz clazz = clazzRepository.findById(clazzId).orElse(null);
            if (clazz != null) {
                emailService.sendCourseRegistrationEmail(principal.getUser(), clazz, period);
            }
        }
        
        if (period.getEffectiveSemester() != null && period.getEffectiveAcademicYear() != null) {
            try {
                tuitionService.generateInvoice(principal.getUser().getId(), period.getEffectiveSemester(), period.getEffectiveAcademicYear());
            } catch (Exception ignored) {}
        }
        
        return responses;
    }

    @Override
    @Transactional
    public void unregister(Long clazzId, UserPrincipal principal) {
        RegistrationPeriod period = getOpenPeriod(); // Kiểm tra đợt đăng ký đang mở
        User student = principal.getUser();

        Enrollment e = enrollmentRepository.findByStudentIdAndClazzId(student.getId(), clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        // Xóa progress trước
        lessonProgressRepository.findByEnrollmentId(e.getId())
                .forEach(lessonProgressRepository::delete);
        enrollmentRepository.delete(e);
        enrollmentRepository.flush(); // Ensure deletion is visible to subsequent queries

        // Cập nhật lại học phí sau khi hủy lớp
        if (period.getEffectiveSemester() != null && period.getEffectiveAcademicYear() != null) {
            try {
                tuitionService.generateInvoice(student.getId(), period.getEffectiveSemester(), period.getEffectiveAcademicYear());
            } catch (Exception ignored) {}
        }
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
        if (!isWithinWindow(LocalDateTime.now(REGISTRATION_ZONE), p.getOpenAt(), p.getCloseAt())) {
            throw new AppException(ErrorCode.REGISTRATION_CLOSED);
        }
        return p;
    }

    static boolean isWithinWindow(LocalDateTime now, LocalDateTime openAt, LocalDateTime closeAt) {
        return (openAt == null || !now.isBefore(openAt))
                && (closeAt == null || now.isBefore(closeAt));
    }

    private static void validateWindow(LocalDateTime open, LocalDateTime close) {
        if (open == null || close == null || !close.isAfter(open)) {
            throw new AppException(ErrorCode.REGISTRATION_CLOSED);
        }
    }

    private void deactivateAll() {
        periodRepository.deactivateAllActive();
        periodRepository.flush();
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
