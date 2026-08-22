package com.ex.learninghub.modules.admin.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

@Component
public class TranscriptPdfExporter {

    public void export(TranscriptData data, HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            Paragraph title = new Paragraph("ACADEMIC TRANSCRIPT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph student = new Paragraph(
                    "Student: " + data.studentName() + " (" + data.studentCode() + ")",
                    FontFactory.getFont(FontFactory.HELVETICA, 12));
            student.setAlignment(Element.ALIGN_CENTER);
            student.setSpacingAfter(18);
            document.add(student);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3f, 2.5f, 1.5f, 1.5f, 1.5f});

            String[] headers = {"Course", "Clazz", "Midterm", "Final", "Total"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(230, 230, 230));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            for (TranscriptRow row : data.rows()) {
                table.addCell(new Phrase(row.courseTitle(), cellFont));
                table.addCell(new Phrase(row.clazzName(), cellFont));
                table.addCell(center(row.midterm(), cellFont));
                table.addCell(center(row.finalScore(), cellFont));
                PdfPCell totalCell = center(row.total(), cellFont);
                totalCell.setBackgroundColor(new Color(245, 245, 245));
                table.addCell(totalCell);
            }

            document.add(table);

            if (data.gpa() != null) {
                Paragraph gpa = new Paragraph("Cumulative Average: "
                        + data.gpa().toPlainString() + " / 10", headerFont);
                gpa.setAlignment(Element.ALIGN_RIGHT);
                gpa.setSpacingBefore(12);
                document.add(gpa);
            }
        } finally {
            document.close();
        }
    }

    private PdfPCell center(String value, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "-", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    public record TranscriptRow(String courseTitle, String clazzName,
                                String midterm, String finalScore, String total) {
    }

    public record TranscriptData(String studentCode, String studentName,
                                 java.math.BigDecimal gpa, List<TranscriptRow> rows) {
    }
}
