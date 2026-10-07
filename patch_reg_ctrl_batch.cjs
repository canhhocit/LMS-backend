const fs = require('fs');
let content = fs.readFileSync('src/main/java/com/ex/learninghub/modules/registration/controller/RegistrationController.java', 'utf8');

const batchEndpoint = `
    @PostMapping("/registration/batch")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Sinh viên đăng ký nhiều lớp học phần",
            description = "Sinh viên đăng ký vào nhiều lớp học phần cùng lúc trong đợt đăng ký hiện tại."
    )
    public ResponseEntity<ApiResponse<List<RegistrationResponse>>> batchRegister(
            @RequestBody List<Long> clazzIds,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.batchRegister(clazzIds, principal)));
    }`;

if (!content.includes('/registration/batch')) {
    content = content.replace('    @DeleteMapping("/registration/{clazzId}")', `${batchEndpoint}\n\n    @DeleteMapping("/registration/{clazzId}")`);
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/registration/controller/RegistrationController.java', content);
}
