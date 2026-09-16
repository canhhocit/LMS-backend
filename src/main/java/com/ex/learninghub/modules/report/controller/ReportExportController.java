package com.ex.learninghub.modules.report.controller;

import com.ex.learninghub.modules.report.service.ExcelReportExporter;
import com.ex.learninghub.modules.report.service.PdfTranscriptExporter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportExportController {

    private final PdfTranscriptExporter pdfTranscriptExporter;
    private final ExcelReportExporter excelReportExporter;

    @GetMapping("/transcript/{studentId}/pdf")
    public ResponseEntity<byte[]> exportTranscriptPdf(@PathVariable Long studentId) {
        byte[] pdfBytes = pdfTranscriptExporter.exportTranscriptPdf(studentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transcript_student_" + studentId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/class/{classId}/excel")
    public ResponseEntity<byte[]> exportClassGradesExcel(@PathVariable Long classId) {
        byte[] excelBytes = excelReportExporter.exportClassGradesExcel(classId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=class_grades_" + classId + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
