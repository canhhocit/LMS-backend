const fs = require('fs');

// 1. Update ScheduleService.java
let svcContent = fs.readFileSync('src/main/java/com/ex/learninghub/modules/schedule/service/ScheduleService.java', 'utf8');
if (!svcContent.includes('getAllAdminSchedules')) {
    svcContent = svcContent.replace(
        'List<ScheduleResponse> getMyWeeklySchedule(UserPrincipal principal);',
        'List<ScheduleResponse> getMyWeeklySchedule(UserPrincipal principal);\n\n    List<ScheduleResponse> getAllAdminSchedules(UserPrincipal principal);'
    );
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/schedule/service/ScheduleService.java', svcContent);
}

// 2. Update ScheduleServiceImpl.java
let implContent = fs.readFileSync('src/main/java/com/ex/learninghub/modules/schedule/service/impl/ScheduleServiceImpl.java', 'utf8');
if (!implContent.includes('getAllAdminSchedules')) {
    const methodStr = `
    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAllAdminSchedules(UserPrincipal principal) {
        return scheduleRepository.findAll().stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }`;
    implContent = implContent.replace(
        'public List<ScheduleResponse> getMyWeeklySchedule(UserPrincipal principal) {',
        `${methodStr}\n\n    @Override\n    @Transactional(readOnly = true)\n    public List<ScheduleResponse> getMyWeeklySchedule(UserPrincipal principal) {`
    );
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/schedule/service/impl/ScheduleServiceImpl.java', implContent);
}

// 3. Update ScheduleController.java
let ctrlContent = fs.readFileSync('src/main/java/com/ex/learninghub/modules/schedule/controller/ScheduleController.java', 'utf8');
if (!ctrlContent.includes('/admin/schedules')) {
    const endpoint = `
    @GetMapping("/admin/schedules")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Lấy toàn bộ lịch học (Admin)", description = "Lấy toàn bộ lịch học để vẽ Overview Timetable.")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getAllAdminSchedules(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(scheduleService.getAllAdminSchedules(principal)));
    }`;
    ctrlContent = ctrlContent.replace(
        'public class ScheduleController {',
        `public class ScheduleController {${endpoint}`
    );
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/schedule/controller/ScheduleController.java', ctrlContent);
}
