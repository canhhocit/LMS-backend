package com.ex.learninghub.modules.course.controller;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.dto.response.ClazzResponse;
import com.ex.learninghub.modules.course.service.ClazzService;
import com.ex.learninghub.modules.enrollment.service.ClazzEnrollmentService;
import com.ex.learninghub.modules.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ex.learninghub.modules.registration.repository.RegistrationPeriodRepository;
import com.ex.learninghub.modules.curriculum.repository.CurriculumCourseRepository;
import com.ex.learninghub.modules.registration.entity.RegistrationPeriod;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.user.entity.User;
import java.util.Optional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/me/classes")
@RequiredArgsConstructor
@Tag(name = "Lớp học phần của tôi", description = "API lấy danh sách lớp học phần theo vai trò của người dùng hiện tại")
public class MyClassController {

    private final ClazzEnrollmentService enrollmentService;
    private final ClazzService clazzService;
    private final RegistrationPeriodRepository periodRepository;
    private final CurriculumCourseRepository curriculumCourseRepository;

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Lấy danh sách lớp học phần của tôi",
            description = "Trả về danh sách lớp học phần tùy theo vai trò: "
                    + "Sinh viên -> các lớp đang theo học; "
                    + "Giảng viên -> các lớp đang phụ trách; "
                    + "Admin -> tất cả lớp học phần."
    )
    public ApiResponse<List<ClazzResponse>> getMyClasses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        Role role = userPrincipal.getUser().getRole();

        List<ClazzResponse> classes;
        if (role == Role.STUDENT) {
            classes = enrollmentService.getClazzesOfStudent(userId);
        } else if (role == Role.LECTURER) {
            classes = clazzService.getClazzesByLecturer(userId);
        } else {
            classes = clazzService.getAllClazzes();
        }

        return ApiResponse.success(classes);
    }

    @GetMapping("/available")
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
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lấy chi tiết lớp học phần", description = "Sinh viên đã ghi danh, giảng viên phụ trách hoặc admin có thể xem chi tiết lớp học phần.")
    public ApiResponse<ClazzResponse> getClazzDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Role role = userPrincipal.getUser().getRole();
        if (role == Role.ADMIN) {
            return ApiResponse.success(clazzService.getClazzById(id));
        }

        List<ClazzResponse> classes = role == Role.STUDENT
                ? enrollmentService.getClazzesOfStudent(userPrincipal.getUser().getId())
                : clazzService.getClazzesByLecturer(userPrincipal.getUser().getId());

        boolean allowed = classes.stream().anyMatch(clazz -> clazz.getId().equals(id));
        if (!allowed) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        return ApiResponse.success(clazzService.getClazzById(id));
    }

    @GetMapping("/{id}/students")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lấy danh sách sinh viên trong lớp học phần", description = "Chỉ người có quyền truy cập lớp mới được xem danh sách sinh viên.")
    public ApiResponse<List<UserResponse>> getStudentsInClazz(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Role role = userPrincipal.getUser().getRole();
        if (role == Role.ADMIN) {
            return ApiResponse.success(enrollmentService.getStudentsInClazz(id));
        }

        boolean allowed = (role == Role.LECTURER && clazzService.getClazzesByLecturer(userPrincipal.getUser().getId())
                .stream().anyMatch(clazz -> clazz.getId().equals(id)))
                || (role == Role.STUDENT && enrollmentService.getClazzesOfStudent(userPrincipal.getUser().getId())
                .stream().anyMatch(clazz -> clazz.getId().equals(id)));

        if (!allowed) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        return ApiResponse.success(enrollmentService.getStudentsInClazz(id));
    }
}
