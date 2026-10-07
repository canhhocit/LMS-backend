const fs = require('fs');

// 1. Service interface
let svc = fs.readFileSync('src/main/java/com/ex/learninghub/modules/assessment/service/AssessmentService.java', 'utf8');
svc = svc.replace('List<AssignmentResponse> getAssignmentsByClass(Long classId);', 'List<AssignmentResponse> getAssignmentsByClass(Long classId, UserPrincipal userPrincipal);');
fs.writeFileSync('src/main/java/com/ex/learninghub/modules/assessment/service/AssessmentService.java', svc);

// 2. Controller
let ctrl = fs.readFileSync('src/main/java/com/ex/learninghub/modules/assessment/controller/AssessmentController.java', 'utf8');
ctrl = ctrl.replace(
    'public ApiResponse<List<AssignmentResponse>> getAssignmentsByClass(@PathVariable Long classId) {',
    'public ApiResponse<List<AssignmentResponse>> getAssignmentsByClass(@PathVariable Long classId, @AuthenticationPrincipal UserPrincipal userPrincipal) {'
);
ctrl = ctrl.replace(
    'return ApiResponse.success(assessmentService.getAssignmentsByClass(classId));',
    'return ApiResponse.success(assessmentService.getAssignmentsByClass(classId, userPrincipal));'
);
fs.writeFileSync('src/main/java/com/ex/learninghub/modules/assessment/controller/AssessmentController.java', ctrl);

// 3. Service Impl
let impl = fs.readFileSync('src/main/java/com/ex/learninghub/modules/assessment/service/impl/AssessmentServiceImpl.java', 'utf8');
const classBefore = `public List<AssignmentResponse> getAssignmentsByClass(Long classId) {
        return assignmentRepository.findByClazzId(classId).stream()`;
const classAfter = `public List<AssignmentResponse> getAssignmentsByClass(Long classId, UserPrincipal userPrincipal) {
        if (userPrincipal.getUser().getRole() == com.ex.learninghub.common.enums.Role.STUDENT) {
            checkStudentEnrollment(classId, userPrincipal.getUser().getId());
        }
        return assignmentRepository.findByClazzId(classId).stream()`;

if (impl.includes(classBefore)) {
    impl = impl.replace(classBefore, classAfter);
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/assessment/service/impl/AssessmentServiceImpl.java', impl);
    console.log("getAssignmentsByClass IDOR patched!");
} else {
    console.log("getAssignmentsByClass not matched.");
}
