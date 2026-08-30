package com.ex.learninghub.modules.course.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.course.dto.request.ClazzRequest;
import com.ex.learninghub.modules.course.dto.response.ClazzResponse;
import com.ex.learninghub.modules.course.service.ClazzService;
import com.ex.learninghub.modules.enrollment.dto.request.EnrollStudentsRequest;
import com.ex.learninghub.modules.enrollment.service.ClazzEnrollmentService;
import com.ex.learninghub.modules.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/classes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') and @adminPermissionService.hasPermission(authentication, 'MANAGE_USERS')")
@Tag(name = "Quản trị - Lớp học phần", description = "Các API CRUD lớp học phần (Clazz) và quản lý sinh viên trong lớp dành cho Admin")
public class AdminClazzController {

    private final ClazzService clazzService;
    private final ClazzEnrollmentService enrollmentService;

    @PostMapping
    @Operation(
            summary = "Tạo lớp học phần mới",
            description = "Tạo một lớp học phần mới (Clazz) cho một khóa học đã có, gắn với giảng viên phụ trách và lịch học."
    )
    public ApiResponse<ClazzResponse> createClazz(@Valid @RequestBody ClazzRequest request) {
        return ApiResponse.success(clazzService.createClazz(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật thông tin lớp học phần",
            description = "Cập nhật thông tin của một lớp học phần đã tồn tại (tên, giảng viên, sĩ số tối đa, ...)."
    )
    public ApiResponse<ClazzResponse> updateClazz(
            @PathVariable Long id,
            @Valid @RequestBody ClazzRequest request) {
        return ApiResponse.success(clazzService.updateClazz(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Xóa lớp học phần",
            description = "Xóa một lớp học phần khỏi hệ thống. Lưu ý: chỉ xóa được khi lớp chưa có sinh viên đăng ký."
    )
    public ApiResponse<Void> deleteClazz(
            @PathVariable Long id) {
        clazzService.deleteClazz(id);
        return ApiResponse.success(null);
    }

    @GetMapping
    @Operation(
            summary = "Lấy danh sách tất cả lớp học phần",
            description = "Trả về toàn bộ danh sách lớp học phần trong hệ thống (không phân trang)."
    )
    public ApiResponse<List<ClazzResponse>> getAllClazzes() {
        return ApiResponse.success(clazzService.getAllClazzes());
    }

    @GetMapping("/paged")
    @Operation(
            summary = "Lấy danh sách lớp học phần (phân trang)",
            description = "Trả về danh sách lớp học phần theo trang, hỗ trợ sắp xếp theo các trường."
    )
    public ApiResponse<Page<ClazzResponse>> getAllClazzesPaged(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ApiResponse.success(clazzService.getAllClazzes(pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy chi tiết một lớp học phần",
            description = "Trả về thông tin chi tiết của lớp học phần theo ID."
    )
    public ApiResponse<ClazzResponse> getClazzById(
            @PathVariable Long id) {
        return ApiResponse.success(clazzService.getClazzById(id));
    }

    @PostMapping("/{id}/enroll")
    @Operation(
            summary = "Thêm sinh viên vào lớp học phần",
            description = "Ghi danh (enroll) một hoặc nhiều sinh viên vào lớp học phần theo danh sách ID."
    )
    public ApiResponse<Void> enrollStudents(
            @PathVariable Long id,
            @Valid @RequestBody EnrollStudentsRequest request) {
        enrollmentService.enrollStudents(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{classId}/students/{studentId}")
    @Operation(
            summary = "Xóa sinh viên khỏi lớp học phần",
            description = "Hủy ghi danh một sinh viên khỏi lớp học phần cụ thể."
    )
    public ApiResponse<Void> removeStudent(
            @PathVariable Long classId,
            @PathVariable Long studentId) {
        enrollmentService.removeStudent(classId, studentId);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}/students")
    @Operation(
            summary = "Lấy danh sách sinh viên của một lớp học phần",
            description = "Trả về danh sách tất cả sinh viên đang theo học trong một lớp học phần."
    )
    public ApiResponse<List<UserResponse>> getStudentsInClazz(
            @PathVariable Long id) {
        return ApiResponse.success(enrollmentService.getStudentsInClazz(id));
    }

    @GetMapping("/lecturer/{lecturerId}")
    @Operation(
            summary = "Lấy danh sách lớp học phần theo giảng viên",
            description = "Trả về tất cả lớp học phần mà một giảng viên đang phụ trách."
    )
    public ApiResponse<List<ClazzResponse>> getClazzesByLecturer(
            @PathVariable Long lecturerId) {
        return ApiResponse.success(clazzService.getClazzesByLecturer(lecturerId));
    }
}
