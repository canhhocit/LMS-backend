const fs = require('fs');
let impl = fs.readFileSync('src/main/java/com/ex/learninghub/modules/assessment/service/impl/AssessmentServiceImpl.java', 'utf8');

const classRegex = /public List<AssignmentResponse> getAssignmentsByClass\(Long classId\) {\s*return assignmentRepository.findByClazzId\(classId\).stream\(\)/;
if (classRegex.test(impl)) {
    impl = impl.replace(classRegex, `public List<AssignmentResponse> getAssignmentsByClass(Long classId, UserPrincipal userPrincipal) {
        if (userPrincipal.getUser().getRole() == com.ex.learninghub.common.enums.Role.STUDENT) {
            checkStudentEnrollment(classId, userPrincipal.getUser().getId());
        }
        return assignmentRepository.findByClazzId(classId).stream()`);
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/assessment/service/impl/AssessmentServiceImpl.java', impl);
    console.log("getAssignmentsByClass IDOR patched using regex!");
} else {
    console.log("Regex still not matched.");
}
