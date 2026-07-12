package com.ex.learninghub.modules.user.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.user.dto.request.UserCreateRequest;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<User> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(userService.createUser(request));
    }

    @PostMapping("/import-students")
    public ApiResponse<List<User>> importStudents(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success(userService.importStudentsFromExcel(file));
    }

    @PostMapping("/import-lecturers")
    public ApiResponse<List<User>> importLecturers(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success(userService.importLecturersFromExcel(file));
    }
}
