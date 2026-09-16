package com.ex.learninghub.modules.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelReportExporter {

    public byte[] exportClassGradesExcel(Long classId) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Class Grades Report");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"STT", "Mã sinh viên", "Họ và tên", "Điểm chuyên cần", "Điểm giữa kỳ", "Điểm cuối kỳ", "Điểm tổng kết", "Xếp loại"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Sample Data rows
            Object[][] data = {
                {1, "SV001", "Nguyễn Văn A", 9.0, 8.5, 8.0, 8.35, "Giỏi"},
                {2, "SV002", "Trần Thị B", 10.0, 9.0, 9.5, 9.45, "Xuất sắc"},
                {3, "SV003", "Lê Hoàng C", 7.0, 6.5, 6.0, 6.35, "Trung bình khá"},
                {4, "SV004", "Phạm Quốc D", 8.5, 8.0, 8.5, 8.40, "Giỏi"}
            };

            int rowIdx = 1;
            for (Object[] rowData : data) {
                Row row = sheet.createRow(rowIdx++);
                for (int colIdx = 0; colIdx < rowData.length; colIdx++) {
                    Cell cell = row.createCell(colIdx);
                    Object val = rowData[colIdx];
                    if (val instanceof Integer) {
                        cell.setCellValue((Integer) val);
                    } else if (val instanceof Double) {
                        cell.setCellValue((Double) val);
                    } else {
                        cell.setCellValue(val.toString());
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            log.info("Xuất báo cáo điểm lớp học dạng Excel thành công cho Class ID {}", classId);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Lỗi tạo file báo cáo Excel: {}", e.getMessage());
            throw new RuntimeException("Thất bại khi tạo file Excel báo cáo điểm", e);
        }
    }
}
