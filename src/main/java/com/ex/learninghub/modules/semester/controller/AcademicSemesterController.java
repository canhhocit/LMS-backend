package com.ex.learninghub.modules.semester.controller;

import com.ex.learninghub.modules.semester.dto.AcademicSemesterRequest;
import com.ex.learninghub.modules.semester.dto.AcademicSemesterResponse;
import com.ex.learninghub.modules.semester.entity.AcademicSemester;
import com.ex.learninghub.modules.semester.entity.SemesterStatus;
import com.ex.learninghub.modules.semester.repository.AcademicSemesterRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/semesters")
@RequiredArgsConstructor
@Tag(name = "Academic Semesters", description = "Quản lý học kỳ - nguồn chính xác duy nhất về thông tin học kỳ trong toàn hệ thống")
public class AcademicSemesterController {

    private final AcademicSemesterRepository repository;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả học kỳ", description = "Trả về các học kỳ sắp xếp theo năm học và số thứ tự giảm dần.")
    public List<AcademicSemesterResponse> getAll() {
        return repository.findAllByOrderByAcademicYearDescSemesterNoDesc()
                .stream().map(AcademicSemesterResponse::from).toList();
    }

    @GetMapping("/active")
    @Operation(summary = "Học kỳ đang hoạt động", description = "Trả về học kỳ có status = ACTIVE. Dùng để hiển thị context học kỳ hiện tại trên UI.")
    public ResponseEntity<AcademicSemesterResponse> getActive() {
        return repository.findFirstByStatus(SemesterStatus.ACTIVE)
                .map(AcademicSemesterResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết học kỳ")
    public ResponseEntity<AcademicSemesterResponse> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(AcademicSemesterResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Tạo học kỳ mới")
    public ResponseEntity<AcademicSemesterResponse> create(@RequestBody AcademicSemesterRequest req) {
        AcademicSemester s = AcademicSemester.builder()
                .code(req.getCode())
                .name(req.getName())
                .academicYear(req.getAcademicYear())
                .semesterNo(req.getSemesterNo())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .status(req.getStatus() != null ? req.getStatus() : SemesterStatus.UPCOMING)
                .build();
        return ResponseEntity.ok(AcademicSemesterResponse.from(repository.save(s)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Cập nhật học kỳ")
    public ResponseEntity<AcademicSemesterResponse> update(@PathVariable Long id, @RequestBody AcademicSemesterRequest req) {
        return repository.findById(id).map(s -> {
            if (req.getName() != null) s.setName(req.getName());
            if (req.getStartDate() != null) s.setStartDate(req.getStartDate());
            if (req.getEndDate() != null) s.setEndDate(req.getEndDate());
            if (req.getStatus() != null) s.setStatus(req.getStatus());
            return ResponseEntity.ok(AcademicSemesterResponse.from(repository.save(s)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Đặt học kỳ thành ACTIVE", description = "Kích hoạt học kỳ này. Học kỳ cũ vẫn giữ nguyên status — Admin cần tự CLOSE học kỳ cũ nếu cần.")
    public ResponseEntity<AcademicSemesterResponse> activate(@PathVariable Long id) {
        return repository.findById(id).map(s -> {
            s.setStatus(SemesterStatus.ACTIVE);
            return ResponseEntity.ok(AcademicSemesterResponse.from(repository.save(s)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Đóng học kỳ")
    public ResponseEntity<AcademicSemesterResponse> close(@PathVariable Long id) {
        return repository.findById(id).map(s -> {
            s.setStatus(SemesterStatus.CLOSED);
            return ResponseEntity.ok(AcademicSemesterResponse.from(repository.save(s)));
        }).orElse(ResponseEntity.notFound().build());
    }
}
