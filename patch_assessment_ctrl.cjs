const fs = require('fs');
let ctrl = fs.readFileSync('src/main/java/com/ex/learninghub/modules/assessment/controller/AssessmentController.java', 'utf8');

const regex = /public ApiResponse<List<AssignmentResponse>> getAssignmentsByClass\(\s*@PathVariable Long classId\)\s*{/;
if (regex.test(ctrl)) {
    ctrl = ctrl.replace(regex, `public ApiResponse<List<AssignmentResponse>> getAssignmentsByClass(
            @PathVariable Long classId, @AuthenticationPrincipal UserPrincipal userPrincipal) {`);
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/assessment/controller/AssessmentController.java', ctrl);
    console.log("AssessmentController patched!");
} else {
    console.log("Regex not matched in AssessmentController");
}
