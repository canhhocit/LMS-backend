package com.ex.learninghub.modules.report;

import com.ex.learninghub.modules.report.service.ExcelReportExporter;
import com.ex.learninghub.modules.report.service.PdfTranscriptExporter;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportExportTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PdfTranscriptExporter pdfTranscriptExporter;

    private ExcelReportExporter excelReportExporter;

    @BeforeEach
    void setUp() {
        excelReportExporter = new ExcelReportExporter();
    }

    @Test
    @DisplayName("Nên tạo file PDF transcript thành công")
    void exportTranscriptPdf_Success() {
        User student = new User();
        student.setId(100L);
        student.setFullName("Nguyễn Văn A");
        student.setStudentCode("SV2024001");
        student.setEmail("student1@learninghub.edu.vn");
        student.setFaculty("Công nghệ thông tin");

        when(userRepository.findById(100L)).thenReturn(Optional.of(student));

        byte[] pdfBytes = pdfTranscriptExporter.exportTranscriptPdf(100L);

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);
    }

    @Test
    @DisplayName("Nên tạo file Excel class grades thành công")
    void exportClassGradesExcel_Success() {
        byte[] excelBytes = excelReportExporter.exportClassGradesExcel(101L);

        assertThat(excelBytes).isNotNull();
        assertThat(excelBytes.length).isGreaterThan(0);
    }
}
