package com.ex.learninghub.modules.course.controller;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.dto.response.ClazzResponse;
import com.ex.learninghub.modules.course.service.ClazzService;
import com.ex.learninghub.modules.enrollment.service.ClazzEnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
@Tag(name = "Lớp học phần của tôi", description = "API lấy danh sách lớp học phần theo vai trò của người dùng hiện tại")
public class MyClassController {

    private final ClazzEnrollmentService enrollmentService;
    private final ClazzService clazzService;

    @GetMapping("/classes")
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
}
