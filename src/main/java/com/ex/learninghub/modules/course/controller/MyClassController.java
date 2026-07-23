package com.ex.learninghub.modules.course.controller;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.dto.response.ClazzResponse;
import com.ex.learninghub.modules.course.service.ClazzService;
import com.ex.learninghub.modules.enrollment.service.EnrollmentService;
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
public class MyClassController {

    private final EnrollmentService enrollmentService;
    private final ClazzService clazzService;

    @GetMapping("/classes")
    @PreAuthorize("isAuthenticated()")
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
