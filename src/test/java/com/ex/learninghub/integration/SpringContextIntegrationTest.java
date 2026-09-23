package com.ex.learninghub.integration;

import com.ex.learninghub.common.ai.AiClientService;
import com.ex.learninghub.common.ai.service.RagService;
import com.ex.learninghub.modules.assessment.service.AiGradingService;
import com.ex.learninghub.modules.content.service.AiLessonSummarizerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "cloudinary.cloud-name=test-cloud",
    "cloudinary.api-key=123456",
    "cloudinary.api-secret=secret"
})
@ActiveProfiles("dev")
class SpringContextIntegrationTest {

    @Autowired
    private AiClientService aiClientService;

    @Autowired
    private RagService ragService;

    @Autowired
    private AiGradingService aiGradingService;

    @Autowired
    private AiLessonSummarizerService aiLessonSummarizerService;

    @Test
    @DisplayName("Integration Test: Context loads successfully with all Spring AI & Core Beans initialized")
    void contextLoads_and_allBeansAreSuccessfullyInitialized() {
        assertThat(aiClientService).isNotNull();
        assertThat(ragService).isNotNull();
        assertThat(aiGradingService).isNotNull();
        assertThat(aiLessonSummarizerService).isNotNull();
    }
}
