const fs = require('fs');

let impl = fs.readFileSync('src/main/java/com/ex/learninghub/modules/assessment/service/impl/AssessmentServiceImpl.java', 'utf8');

// Add enrollment repository
if (!impl.includes('EnrollmentRepository enrollmentRepository')) {
    impl = impl.replace(
        'private final ClazzRepository clazzRepository;',
        'private final ClazzRepository clazzRepository;\n    private final com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository enrollmentRepository;'
    );
}

// Add student auth helper
const authHelper = `
    private void checkStudentEnrollment(Long classId, Long studentId) {
        if (!enrollmentRepository.existsByStudentIdAndClazzId(studentId, classId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }
`;
if (!impl.includes('checkStudentEnrollment')) {
    impl = impl.replace(
        'private void verifyLecturerOwnsClazz',
        authHelper + '\n    private void verifyLecturerOwnsClazz'
    );
}

// Update submitAssignment
impl = impl.replace(
    'User student = userPrincipal.getUser();\n        if (submissionRepository.findByAssignmentIdAndStudentId(assignmentId, student.getId()).isPresent()) {',
    'User student = userPrincipal.getUser();\n        checkStudentEnrollment(assignment.getClazz().getId(), student.getId());\n        if (submissionRepository.findByAssignmentIdAndStudentId(assignmentId, student.getId()).isPresent()) {'
);

// Update uploadSubmissionFiles
const uploadBefore = `    public List<String> uploadSubmissionFiles(Long assignmentId, List<MultipartFile> files, UserPrincipal userPrincipal) {
        if (!assignmentRepository.existsById(assignmentId)) {
            throw new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND);
        }
        if (files == null || files.isEmpty()) {`;
        
const uploadAfter = `    public List<String> uploadSubmissionFiles(Long assignmentId, List<MultipartFile> files, UserPrincipal userPrincipal) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        if (userPrincipal.getUser().getRole() == com.ex.learninghub.common.enums.Role.STUDENT) {
            checkStudentEnrollment(assignment.getClazz().getId(), userPrincipal.getUser().getId());
        }
        if (files == null || files.isEmpty()) {`;

if (impl.includes(uploadBefore)) {
    impl = impl.replace(uploadBefore, uploadAfter);
}

// Check getAssignmentsByClass needs UserPrincipal
// Wait, AssessmentService interface needs updating first for getAssignmentsByClass!
// So let's skip getAssignmentsByClass for a moment, or let's update it.
// The user constraint says "STUDENT Chỉ được read assignment thuộc class mình enrolled"
// I must update getAssignmentsByClass.
fs.writeFileSync('src/main/java/com/ex/learninghub/modules/assessment/service/impl/AssessmentServiceImpl.java', impl);
console.log("Assignment IDOR patched!");
