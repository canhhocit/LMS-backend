package com.ex.learninghub.modules.user.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.user.dto.request.UserCreateRequest;
import com.ex.learninghub.modules.user.dto.response.UserResponse;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Quản trị người dùng", description = "CRUD tài khoản sinh viên / giảng viên, import/export Excel, reset mật khẩu")
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Tạo người dùng mới", description = "Admin tạo mới một tài khoản (sinh viên/giảng viên/admin).")
    public ApiResponse<User> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(userService.createUser(request));
    }

    @PostMapping("/import-students")
    @Operation(summary = "Import danh sách sinh viên từ Excel", description = "Admin upload file Excel để tạo hàng loạt tài khoản sinh viên.")
    public ApiResponse<List<User>> importStudents(@RequestPart("file") MultipartFile file) {
        return ApiResponse.success(userService.importStudentsFromExcel(file));
    }

    @PostMapping("/import-lecturers")
    @Operation(summary = "Import danh sách giảng viên từ Excel", description = "Admin upload file Excel để tạo hàng loạt tài khoản giảng viên.")
    public ApiResponse<List<User>> importLecturers(@RequestPart("file") MultipartFile file) {
        return ApiResponse.success(userService.importLecturersFromExcel(file));
    }

    @GetMapping("/students")
    @Operation(summary = "Danh sách sinh viên (phân trang)", description = "Trả về danh sách sinh viên có phân trang, hỗ trợ tìm kiếm theo từ khóa.")
    public ApiResponse<Page<UserResponse>> getStudents(
            @RequestParam(required = false) String keyword,
            @ParameterObject @PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        return ApiResponse.success(userService.getStudents(keyword, pageable));
    }

    @GetMapping("/lecturers")
    @Operation(summary = "Danh sách giảng viên (phân trang)", description = "Trả về danh sách giảng viên có phân trang, hỗ trợ tìm kiếm theo từ khóa.")
    public ApiResponse<Page<UserResponse>> getLecturers(
            @RequestParam(required = false) String keyword,
            @ParameterObject @PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        return ApiResponse.success(userService.getLecturers(keyword, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết người dùng", description = "Trả về thông tin chi tiết của một người dùng theo ID.")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    @GetMapping("/students/export")
    @Operation(summary = "Export danh sách sinh viên ra Excel", description = "Tải xuống file Excel chứa danh sách sinh viên (có thể lọc theo từ khóa).")
    public ResponseEntity<byte[]> exportStudents(
            @RequestParam(required = false) String keyword) throws IOException {
        byte[] data = userService.exportStudentsToExcel(keyword);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"students.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @GetMapping("/lecturers/export")
    @Operation(summary = "Export danh sách giảng viên ra Excel", description = "Tải xuống file Excel chứa danh sách giảng viên (có thể lọc theo từ khóa).")
    public ResponseEntity<byte[]> exportLecturers(
            @RequestParam(required = false) String keyword) throws IOException {
        byte[] data = userService.exportLecturersToExcel(keyword);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"lecturers.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @PostMapping("/{id}/reset-password")
    @Operation(summary = "Reset mật khẩu người dùng", description = "Admin đặt lại mật khẩu của một người dùng về mặc định.")
    public ApiResponse<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin người dùng", description = "Admin cập nhật thông tin (họ tên, email, vai trò, ...) của một người dùng.")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long id,
                                                 @Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa người dùng", description = "Admin xóa (hoặc vô hiệu hóa) một tài khoản người dùng.")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success(null);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cập nhật trạng thái người dùng", description = "Admin thay đổi trạng thái tài khoản (ACTIVE, INACTIVE, LOCKED, ...).")
    public ApiResponse<Void> updateUserStatus(@PathVariable Long id,
                                               @RequestParam String status) {
        userService.updateUserStatus(id, status);
        return ApiResponse.success(null);
    }
}
