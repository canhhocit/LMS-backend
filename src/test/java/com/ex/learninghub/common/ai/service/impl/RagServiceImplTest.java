package com.ex.learninghub.common.ai.service.impl;

import com.ex.learninghub.common.ai.AiClientService;
import com.ex.learninghub.common.ai.dto.RagIngestRequest;
import com.ex.learninghub.common.ai.dto.RagQueryRequest;
import com.ex.learninghub.common.ai.dto.RagQueryResponse;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.enrollment.entity.Enrollment;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RagServiceImplTest {

    @Mock
    private AiClientService aiClientService;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private RagServiceImpl ragService;

    private UserPrincipal studentPrincipal;
    private UserPrincipal adminPrincipal;

    @BeforeEach
    void setUp() {
        // Clear documents
        try {
            var field = RagServiceImpl.class.getDeclaredField("documentStore");
            field.setAccessible(true);
            var store = (java.util.List<?>) field.get(ragService);
            store.clear();
        } catch (Exception ignored) {}

        User student = new User();
        student.setId(100L);
        studentPrincipal = new UserPrincipal(student, List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));

        User admin = new User();
        admin.setId(999L);
        adminPrincipal = new UserPrincipal(admin, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testQuery_StudentNotEnrolled_ThrowsException() {
        RagQueryRequest queryRequest = new RagQueryRequest();
        queryRequest.setQuestion("Test");
        queryRequest.setClazzId(1L);

        when(enrollmentRepository.existsByStudentIdAndClazzId(100L, 1L)).thenReturn(false);

        assertThrows(AppException.class, () -> ragService.queryCourseMaterials(queryRequest, studentPrincipal));
    }

    @Test
    void testQuery_StudentEnrolled_ReturnsResult() {
        when(enrollmentRepository.existsByStudentIdAndClazzId(100L, 1L)).thenReturn(true);
        when(aiClientService.isAiConfigured()).thenReturn(false); // Rule based fallback

        RagIngestRequest ingestRequest = new RagIngestRequest();
        ingestRequest.setDocumentContent("Đây là tài liệu ôn tập môn Java. Lập trình hướng đối tượng rất quan trọng.");
        ingestRequest.setSourceName("Java_Doc");
        ingestRequest.setClazzId(1L);
        ragService.ingestDocument(ingestRequest, adminPrincipal);

        RagQueryRequest queryRequest = new RagQueryRequest();
        queryRequest.setQuestion("hướng đối tượng");
        queryRequest.setClazzId(1L);

        RagQueryResponse response = ragService.queryCourseMaterials(queryRequest, studentPrincipal);

        assertTrue(response.getIsRagUsed());
        assertEquals(1, response.getRetrievedCount());
        assertTrue(response.getAnswer().contains("hướng đối tượng"));
    }

    @Test
    void testQuery_AiConfigured_CallsAi() throws Exception {
        when(enrollmentRepository.existsByStudentIdAndClazzId(100L, 1L)).thenReturn(true);
        when(aiClientService.isAiConfigured()).thenReturn(true);
        when(aiClientService.generateContent(anyString(), anyString())).thenReturn("AI đã tìm thấy kết quả RAG.");

        RagIngestRequest ingestRequest = new RagIngestRequest();
        ingestRequest.setDocumentContent("Spring Boot là framework Java.");
        ingestRequest.setSourceName("Spring_Doc");
        ingestRequest.setClazzId(1L);
        ragService.ingestDocument(ingestRequest, adminPrincipal);

        RagQueryRequest queryRequest = new RagQueryRequest();
        queryRequest.setQuestion("Spring Boot");
        queryRequest.setClazzId(1L);

        RagQueryResponse response = ragService.queryCourseMaterials(queryRequest, studentPrincipal);

        assertEquals("AI đã tìm thấy kết quả RAG.", response.getAnswer());
        verify(aiClientService, times(1)).generateContent(anyString(), anyString());
    }

    @Test
    void testQuery_NoDocument_ReturnsEmptyWarning() {
        when(enrollmentRepository.existsByStudentIdAndClazzId(100L, 1L)).thenReturn(true);

        RagQueryRequest queryRequest = new RagQueryRequest();
        queryRequest.setQuestion("Kotlin");
        queryRequest.setClazzId(1L);

        RagQueryResponse response = ragService.queryCourseMaterials(queryRequest, studentPrincipal);

        assertFalse(response.getIsRagUsed());
        assertTrue(response.getAnswer().contains("không có tài liệu bài giảng nào"));
    }
}
