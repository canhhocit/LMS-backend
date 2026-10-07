const fs = require('fs');
let content = fs.readFileSync('src/main/java/com/ex/learninghub/modules/registration/service/impl/RegistrationServiceImpl.java', 'utf8');

const regex = /public void unregister\(Long clazzId, UserPrincipal principal\) \{[\s\S]*?enrollmentRepository\.delete\(e\);\s*\}/m;

const newUnregister = `public void unregister(Long clazzId, UserPrincipal principal) {
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
    }`;

content = content.replace(regex, newUnregister);
fs.writeFileSync('src/main/java/com/ex/learninghub/modules/registration/service/impl/RegistrationServiceImpl.java', content);
