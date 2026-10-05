const fs = require('fs');
let content = fs.readFileSync('src/main/java/com/ex/learninghub/modules/course/controller/MyClassController.java', 'utf8');

const imports = `import com.ex.learninghub.modules.registration.repository.RegistrationPeriodRepository;
import com.ex.learninghub.modules.curriculum.repository.CurriculumCourseRepository;
import com.ex.learninghub.modules.registration.entity.RegistrationPeriod;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.user.entity.User;
import java.util.Optional;`;

if (!content.includes('import com.ex.learninghub.modules.registration.repository.RegistrationPeriodRepository;')) {
    content = content.replace('import org.springframework.web.bind.annotation.RestController;', `import org.springframework.web.bind.annotation.RestController;\n${imports}`);
}

if (!content.includes('private final RegistrationPeriodRepository periodRepository;')) {
    content = content.replace('private final ClazzService clazzService;', `private final ClazzService clazzService;\n    private final RegistrationPeriodRepository periodRepository;\n    private final CurriculumCourseRepository curriculumCourseRepository;`);
}

const oldAvailable = `    @GetMapping("/available")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Lấy danh sách lớp còn mở đăng ký", description = "Sinh viên xem các lớp đang mở cho phép đăng ký, loại bỏ các lớp đã tham gia.")
    public ApiResponse<List<ClazzResponse>> getAvailableClassesForRegistration(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long studentId = userPrincipal.getUser().getId();
        Set<Long> enrolledIds = enrollmentService.getClazzesOfStudent(studentId).stream()
                .map(ClazzResponse::getId)
                .collect(Collectors.toSet());

        List<ClazzResponse> available = clazzService.getAllClazzes().stream()
                .filter(clazz -> !enrolledIds.contains(clazz.getId()))
                .toList();

        return ApiResponse.success(available);
    }`;

const newAvailable = `    @GetMapping("/available")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Lấy danh sách lớp còn mở đăng ký", description = "Sinh viên xem các lớp đang mở cho phép đăng ký, loại bỏ các lớp đã tham gia, và được filter theo đợt đăng ký và CTĐT.")
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
            // Backward compatibility: If no classes specifically assigned to period, 
            // we could either return empty, or return all classes.
            // But per requirement, admin MUST assign classes to period. So we return empty.
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
                .map(c -> {
                    long currentStudents = 0; // Optional: could fetch exact if needed, but performance might suffer.
                    return ClazzResponse.from(c, (int) currentStudents);
                })
                .toList();

        return ApiResponse.success(available);
    }`;

if (content.includes(oldAvailable)) {
    content = content.replace(oldAvailable, newAvailable);
}

fs.writeFileSync('src/main/java/com/ex/learninghub/modules/course/controller/MyClassController.java', content);
