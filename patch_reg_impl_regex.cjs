const fs = require('fs');
let content = fs.readFileSync('src/main/java/com/ex/learninghub/modules/registration/service/impl/RegistrationServiceImpl.java', 'utf8');

const regex = /@Override\s+@Transactional\s+public RegistrationResponse register\(Long clazzId, UserPrincipal principal\) \{[\s\S]*?@Override\s+@Transactional\s+public void unregister\(Long clazzId, UserPrincipal principal\) \{/m;

const internalMethodBody = `    @Transactional
    public RegistrationResponse registerInternal(Long clazzId, UserPrincipal principal, RegistrationPeriod period) {
        User student = principal.getUser();

        if (enrollmentRepository.existsByStudentIdAndClazzId(student.getId(), clazzId)) {
            throw new AppException(ErrorCode.ENROLLMENT_EXISTS);
        }

        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

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
        
        return RegistrationResponse.from(savedEnrollment, clazz);
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
    public void unregister(Long clazzId, UserPrincipal principal) {`;

content = content.replace(regex, internalMethodBody);
fs.writeFileSync('src/main/java/com/ex/learninghub/modules/registration/service/impl/RegistrationServiceImpl.java', content);
