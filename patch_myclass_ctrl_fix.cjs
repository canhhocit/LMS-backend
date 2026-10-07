const fs = require('fs');
let content = fs.readFileSync('src/main/java/com/ex/learninghub/modules/course/controller/MyClassController.java', 'utf8');

const regex = /@GetMapping\("\/available"\)[\s\S]*?public ApiResponse<List<ClazzResponse>> getAvailableClassesForRegistration\([\s\S]*?return ApiResponse\.success\(available\);\s*\}/m;

const replacement = `@GetMapping("/available")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Lấy danh sách lớp còn mở đăng ký", description = "Sinh viên xem các lớp đang mở cho phép đăng ký, đã filter theo đợt và CTĐT.")
    public ApiResponse<List<ClazzResponse>> getAvailableClassesForRegistration(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User student = userPrincipal.getUser();
        Long studentId = student.getId();

        // 1. Get active period's allowed classes
        Optional<RegistrationPeriod> periodOpt = periodRepository.findActiveWithClasses();
        if (periodOpt.isEmpty()) {
            return ApiResponse.success(List.of());
        }
        
        Set<Clazz> allowedClasses = periodOpt.get().getAllowedClasses();
        if (allowedClasses.isEmpty()) {
            return ApiResponse.success(List.of());
        }
        
        // 2. Get student's curriculum courseIds
        Set<Long> curriculumCourseIds = new java.util.HashSet<>();
        if (student.getCurriculum() != null) {
            curriculumCourseIds = curriculumCourseRepository
                .findByCurriculumId(student.getCurriculum().getId())
                .stream()
                .map(cc -> cc.getCourseId())
                .collect(Collectors.toSet());
        }
        final Set<Long> finalCurrIds = curriculumCourseIds;

        // 3. Get enrolled class IDs
        Set<Long> enrolledIds = enrollmentService.getClazzesOfStudent(studentId).stream()
                .map(ClazzResponse::getId)
                .collect(Collectors.toSet());

        // 4. Filter and map
        List<ClazzResponse> available = allowedClasses.stream()
                .filter(c -> !enrolledIds.contains(c.getId()))
                .filter(c -> finalCurrIds.isEmpty() || 
                    (c.getCourse() != null && finalCurrIds.contains(c.getCourse().getId())))
                .map(c -> ClazzResponse.from(c, 0L))
                .toList();

        return ApiResponse.success(available);
    }`;

content = content.replace(regex, replacement);
fs.writeFileSync('src/main/java/com/ex/learninghub/modules/course/controller/MyClassController.java', content);
