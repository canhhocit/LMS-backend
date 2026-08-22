package com.ex.learninghub.modules.user.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.user.dto.request.UserCreateRequest;
import com.ex.learninghub.modules.user.dto.response.UserResponse;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<User> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(userService.createUser(request));
    }

    @PostMapping("/import-students")
    public ApiResponse<List<User>> importStudents(@RequestPart("file") MultipartFile file) {
        return ApiResponse.success(userService.importStudentsFromExcel(file));
    }

    @PostMapping("/import-lecturers")
    public ApiResponse<List<User>> importLecturers(@RequestPart("file") MultipartFile file) {
        return ApiResponse.success(userService.importLecturersFromExcel(file));
    }

    @GetMapping("/students")
    public ApiResponse<Page<UserResponse>> getStudents(
            @RequestParam(required = false) String keyword,
            @ParameterObject @PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        return ApiResponse.success(userService.getStudents(keyword, pageable));
    }

    @GetMapping("/lecturers")
    public ApiResponse<Page<UserResponse>> getLecturers(
            @RequestParam(required = false) String keyword,
            @ParameterObject @PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        return ApiResponse.success(userService.getLecturers(keyword, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    @GetMapping("/students/export")
    public ResponseEntity<byte[]> exportStudents(
            @RequestParam(required = false) String keyword) throws IOException {
        byte[] data = userService.exportStudentsToExcel(keyword);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"students.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @GetMapping("/lecturers/export")
    public ResponseEntity<byte[]> exportLecturers(
            @RequestParam(required = false) String keyword) throws IOException {
        byte[] data = userService.exportLecturersToExcel(keyword);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"lecturers.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @PostMapping("/{id}/reset-password")
    public ApiResponse<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long id,
                                                 @Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success(null);
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateUserStatus(@PathVariable Long id,
                                               @RequestParam String status) {
        userService.updateUserStatus(id, status);
        return ApiResponse.success(null);
    }
}
