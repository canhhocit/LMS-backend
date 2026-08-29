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
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Quản trị - Báo cáo & Thống kê", description = "Các API phục vụ báo cáo, thống kê và xuất dữ liệu (Excel/PDF) cho Admin")
public class AdminReportController {

    private final AdminService adminService;
    private final ClazzRepository clazzRepository;
    private final GradeRepository gradeRepository;
    private final GradeExcelExporter gradeExcelExporter;
    private final com.ex.learninghub.modules.user.repository.UserRepository userRepository;
    private final TranscriptPdfExporter transcriptPdfExporter;

    @GetMapping("/reports/enrollments-by-month")
    @Operation(
            summary = "Thống kê lượt đăng ký theo tháng",
            description = "Trả về dữ liệu số lượt đăng ký lớp học phần theo từng tháng, phục vụ biểu đồ báo cáo của Admin."
    )
    public ApiResponse<Map<String, Object>> getEnrollmentsByMonth() {
        return ApiResponse.success(adminService.getEnrollmentsByMonth());
    }

    @GetMapping("/reports/average-score-by-clazz")
    @Operation(
            summary = "Thống kê điểm trung bình theo lớp học phần",
            description = "Trả về điểm trung bình tổng kết của từng lớp học phần, phục vụ báo cáo chất lượng đào tạo."
    )
    public ApiResponse<Map<String, Double>> getAverageScoreByClazz() {
        return ApiResponse.success(adminService.getAverageScoreByClazz());
    }

    @GetMapping("/reports/enrollments-by-month/export")
    @Operation(
            summary = "Xuất thống kê đăng ký theo tháng ra Excel",
            description = "Tải xuống file Excel chứa số lượng đăng ký theo từng tháng phục vụ báo cáo admin."
    )
    public void exportEnrollmentsByMonth(HttpServletResponse response) throws IOException {
        Map<String, Object> result = adminService.getEnrollmentsByMonth();
        @SuppressWarnings("unchecked")
        Map<String, Long> series = (Map<String, Long>) result.getOrDefault("series", Map.of());

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("EnrollmentsByMonth");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Month");
            header.createCell(1).setCellValue("Enrollments");

            int rowIndex = 1;
            for (Map.Entry<String, Long> entry : series.entrySet()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

            for (int i = 0; i < 2; i++) {
                sheet.autoSizeColumn(i);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=enrollments-by-month.xlsx");
            workbook.write(response.getOutputStream());
        }
    }

    @GetMapping("/reports/average-score-by-clazz/export")
    @Operation(
            summary = "Xuất điểm trung bình theo lớp ra PDF",
            description = "Tải xuống file PDF tổng hợp điểm trung bình của từng lớp học phần."
    )
    public void exportAverageScoreByClazz(HttpServletResponse response) throws DocumentException, IOException {
        Map<String, Double> scores = adminService.getAverageScoreByClazz();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=average-score-by-class.pdf");

        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            Paragraph title = new Paragraph("Average Score by Class", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3.5f, 1.5f});

            String[] headers = {"Class", "Average Score"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(230, 230, 230));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            for (Map.Entry<String, Double> entry : scores.entrySet()) {
                table.addCell(new Phrase(entry.getKey(), cellFont));
                PdfPCell scoreCell = new PdfPCell(new Phrase(String.format("%.2f", entry.getValue()), cellFont));
                scoreCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(scoreCell);
            }

            document.add(table);
        } finally {
            document.close();
        }
    }

    @GetMapping("/reports/clazz/{clazzId}/export")
    @Operation(
            summary = "Xuất bảng điểm lớp học phần ra Excel",
            description = "Xuất danh sách điểm (giữa kỳ, cuối kỳ, tổng kết) của tất cả sinh viên trong một lớp học phần ra file Excel (.xlsx)."
    )
    public void exportClazzGrades(
            @PathVariable Long clazzId,
            HttpServletResponse response) throws IOException {
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
    @Operation(
            summary = "Xuất bảng điểm (transcript) của sinh viên ra PDF",
            description = "Xuất bảng điểm toàn khóa của một sinh viên ra file PDF, bao gồm GPA và danh sách điểm các môn đã học."
    )
    public void exportTranscript(
            @PathVariable Long studentId,
            HttpServletResponse response) throws IOException {
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
