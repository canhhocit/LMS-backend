const fs = require('fs');
let impl = fs.readFileSync('src/main/java/com/ex/learninghub/modules/grading/service/impl/GradingServiceImpl.java', 'utf8');

const regex = /public List<AttendanceResponse> getMyAttendance\(Long classId, UserPrincipal userPrincipal\) {/;

if (regex.test(impl)) {
    // Add enrollment check
    impl = impl.replace(regex, `public List<AttendanceResponse> getMyAttendance(Long classId, UserPrincipal userPrincipal) {
        if (!enrollmentRepository.existsByStudentIdAndClazzId(userPrincipal.getUser().getId(), classId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }`);
    
    // Make sure EnrollmentRepository is injected
    if (!impl.includes('EnrollmentRepository enrollmentRepository;')) {
        impl = impl.replace(
            'private final ClazzRepository clazzRepository;',
            'private final ClazzRepository clazzRepository;\n    private final com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository enrollmentRepository;'
        );
    }
    
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/grading/service/impl/GradingServiceImpl.java', impl);
    console.log("getMyAttendance IDOR patched!");
} else {
    console.log("Not matched");
}
