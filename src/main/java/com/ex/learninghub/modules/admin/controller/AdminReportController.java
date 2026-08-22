package com.ex.learninghub.modules.admin.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.admin.service.AdminService;
import com.ex.learninghub.modules.admin.service.GradeExcelExporter;
import com.ex.learninghub.modules.admin.service.TranscriptPdfExporter;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.grading.entity.Grade;
import com.ex.learninghub.modules.grading.repository.GradeRepository;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final AdminService adminService;
    private final ClazzRepository clazzRepository;
    private final GradeRepository gradeRepository;
    private final GradeExcelExporter gradeExcelExporter;
    private final com.ex.learninghub.modules.user.repository.UserRepository userRepository;
    private final TranscriptPdfExporter transcriptPdfExporter;

    @GetMapping("/reports/enrollments-by-month")
    public ApiResponse<Map<String, Object>> getEnrollmentsByMonth() {
        return ApiResponse.success(adminService.getEnrollmentsByMonth());
    }

    @GetMapping("/reports/average-score-by-clazz")
    public ApiResponse<Map<String, Double>> getAverageScoreByClazz() {
        return ApiResponse.success(adminService.getAverageScoreByClazz());
    }

    @GetMapping("/reports/clazz/{clazzId}/export")
    public void exportClazzGrades(@PathVariable Long clazzId, HttpServletResponse response) throws IOException {
        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

        List<Grade> grades = gradeRepository.findByClazzId(clazzId);
        List<GradeExcelExporter.GradeExportRow> rows = grades.stream()
                .map(g -> new GradeExcelExporter.GradeExportRow(
                        g.getStudent().getStudentCode() != null
                                ? g.getStudent().getStudentCode()
                                : g.getStudent().getId().toString(),
                        g.getStudent().getFullName(),
                        clazz.getClassName(),
                        g.getMidtermScore(),
                        g.getFinalScore(),
                        g.getTotalScore()))
                .toList();

        gradeExcelExporter.export(rows, clazz.getClassName(), response);
    }

    @GetMapping("/reports/transcript/{studentId}/export")
    public void exportTranscript(@PathVariable Long studentId, HttpServletResponse response) throws IOException {
        com.ex.learninghub.modules.user.entity.User student = userRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Reuse grading service logic: build transcript from grades of this student
        List<Grade> grades = gradeRepository.findByStudentId(studentId);

        java.math.BigDecimal gpa = grades.stream()
                .map(Grade::getTotalScore)
                .filter(s -> s != null)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
                .divide(java.math.BigDecimal.valueOf(Math.max(1, grades.stream()
                        .map(Grade::getTotalScore).filter(s -> s != null).count())),
                        2, java.math.RoundingMode.HALF_UP);
        if (gpa.compareTo(java.math.BigDecimal.ZERO) == 0) {
            gpa = null;
        }

        List<TranscriptPdfExporter.TranscriptRow> rows = grades.stream()
                .map(g -> new TranscriptPdfExporter.TranscriptRow(
                        g.getClazz().getCourse() != null ? g.getClazz().getCourse().getTitle() : "-",
                        g.getClazz().getClassName(),
                        g.getMidtermScore() != null ? g.getMidtermScore().toPlainString() : null,
                        g.getFinalScore() != null ? g.getFinalScore().toPlainString() : null,
                        g.getTotalScore() != null ? g.getTotalScore().toPlainString() : null))
                .toList();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=transcript-" + (student.getStudentCode() != null
                        ? student.getStudentCode() : student.getId()) + ".pdf");
        transcriptPdfExporter.export(
                new TranscriptPdfExporter.TranscriptData(
                        student.getStudentCode() != null ? student.getStudentCode() : student.getId().toString(),
                        student.getFullName(),
                        gpa,
                        rows),
                response);
    }
}
