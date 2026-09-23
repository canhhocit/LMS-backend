package com.ex.learninghub.common.ai.service.impl;

import com.ex.learninghub.common.ai.AiClientService;
import com.ex.learninghub.common.ai.dto.RagIngestRequest;
import com.ex.learninghub.common.ai.dto.RagQueryRequest;
import com.ex.learninghub.common.ai.dto.RagQueryResponse;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class RagServiceImplTest {

    @Mock
    private AiClientService aiClientService;

    @InjectMocks
    private RagServiceImpl ragService;

    private UserPrincipal studentPrincipal;

    @BeforeEach
    void setUp() {
        lenient().when(aiClientService.isAiConfigured()).thenReturn(false);

        User student = User.builder().email("student@test.com").role(Role.STUDENT).build();
        student.setId(100L);
        studentPrincipal = new UserPrincipal(student);
    }

    @Test
    void ingestDocument_and_queryCourseMaterials_success() {
        RagIngestRequest ingestReq = RagIngestRequest.builder()
                .clazzId(10L)
                .lessonId(20L)
                .sourceName("Slide Bài 1: OOP")
                .documentContent("Tính Đóng gói (Encapsulation) là kỹ thuật che giấu dữ liệu bên trong Class bằng access modifier private và chỉ truy cập qua getter/setter.")
                .build();

        ragService.ingestDocument(ingestReq, studentPrincipal);

        RagQueryRequest queryReq = RagQueryRequest.builder()
                .clazzId(10L)
                .lessonId(20L)
                .question("Encapsulation trong Java là gì?")
                .topK(2)
                .build();

        RagQueryResponse response = ragService.queryCourseMaterials(queryReq, studentPrincipal);

        assertThat(response).isNotNull();
        assertThat(response.getQuestion()).isEqualTo("Encapsulation trong Java là gì?");
        assertThat(response.getRetrievedCount()).isGreaterThan(0);
        assertThat(response.getAnswer()).contains("Encapsulation");
        assertThat(response.getRetrievedSources()).isNotEmpty();
    }
}
