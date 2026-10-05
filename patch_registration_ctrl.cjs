const fs = require('fs');
let content = fs.readFileSync('src/main/java/com/ex/learninghub/modules/registration/controller/RegistrationController.java', 'utf8');

const endpoints = `
    @PostMapping("/admin/registration-periods/{periodId}/classes/{clazzId}")
    @PreAuthorize("hasPermission(null, 'MANAGE_REGISTRATION')")
    @Operation(summary = "Gán lớp học phần vào đợt đăng ký")
    public ResponseEntity<ApiResponse<Void>> addClazz(
            @PathVariable Long periodId, @PathVariable Long clazzId) {
        registrationService.addClazzToPeriod(periodId, clazzId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/admin/registration-periods/{periodId}/classes/{clazzId}")
    @PreAuthorize("hasPermission(null, 'MANAGE_REGISTRATION')")
    @Operation(summary = "Gỡ lớp học phần khỏi đợt đăng ký")
    public ResponseEntity<ApiResponse<Void>> removeClazz(
            @PathVariable Long periodId, @PathVariable Long clazzId) {
        registrationService.removeClazzFromPeriod(periodId, clazzId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/admin/registration-periods/{periodId}/classes")
    @PreAuthorize("hasPermission(null, 'MANAGE_REGISTRATION')")
    @Operation(summary = "Lấy danh sách lớp học phần trong đợt đăng ký")
    public ResponseEntity<ApiResponse<List<com.ex.learninghub.modules.course.dto.response.ClazzResponse>>> getClazzes(
            @PathVariable Long periodId) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.getClazzesInPeriod(periodId)));
    }

    // =================== Student self-registration ===================`;

if (!content.includes('addClazz(')) {
    content = content.replace('    // =================== Student self-registration ===================', endpoints);
}
fs.writeFileSync('src/main/java/com/ex/learninghub/modules/registration/controller/RegistrationController.java', content);
