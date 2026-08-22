package com.ex.learninghub.modules.admin.service;

import com.ex.learninghub.modules.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class GradeExcelExporter {

    private static final String[] HEADERS = {
            "No", "Student Code", "Full Name", "Class", "Midterm", "Final", "Total"
    };

    public void export(List<GradeExportRow> rows, String clazzName, HttpServletResponse response)
            throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Grades " + clazzName);

            // Header row
            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(HEADERS[i]);
            }

            // Data rows
            int idx = 1;
            for (GradeExportRow row : rows) {
                Row dataRow = sheet.createRow(idx++);
                dataRow.createCell(0).setCellValue(idx - 1);
                dataRow.createCell(1).setCellValue(row.studentCode());
                dataRow.createCell(2).setCellValue(row.fullName());
                dataRow.createCell(3).setCellValue(row.clazzName());
                if (row.midterm() != null) dataRow.createCell(4).setCellValue(row.midterm().doubleValue());
                if (row.finalScore() != null) dataRow.createCell(5).setCellValue(row.finalScore().doubleValue());
                if (row.total() != null) dataRow.createCell(6).setCellValue(row.total().doubleValue());
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=grades-" + clazzName.replaceAll("\\s+", "_") + ".xlsx");
            workbook.write(response.getOutputStream());
        }
    }

    public record GradeExportRow(
            String studentCode,
            String fullName,
            String clazzName,
            java.math.BigDecimal midterm,
            java.math.BigDecimal finalScore,
            java.math.BigDecimal total) {
    }
}
