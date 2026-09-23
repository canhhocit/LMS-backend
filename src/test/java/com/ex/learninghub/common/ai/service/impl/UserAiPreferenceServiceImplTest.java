package com.ex.learninghub.common.ai.service.impl;

import com.ex.learninghub.common.ai.dto.UserAiPreferenceRequest;
import com.ex.learninghub.common.ai.dto.UserAiPreferenceResponse;
import com.ex.learninghub.common.ai.entity.UserAiPreference;
import com.ex.learninghub.common.ai.repository.UserAiPreferenceRepository;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAiPreferenceServiceImplTest {

    @Mock
    private UserAiPreferenceRepository preferenceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAiPreferenceServiceImpl userAiPreferenceService;

    private User testUser;
    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        testUser = User.builder().fullName("Nguyen Van A").email("a@test.com").build();
        testUser.setId(1L);

        principal = new UserPrincipal(testUser);
    }

    @Test
    void getMyAiPreference_returnsDefault_whenNotFound() {
        when(preferenceRepository.findByUserId(1L)).thenReturn(Optional.empty());

        UserAiPreferenceResponse response = userAiPreferenceService.getMyAiPreference(principal);

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getAiTone()).isEqualTo("FRIENDLY");
        assertThat(response.getResponseLength()).isEqualTo("DETAILED");
    }

    @Test
    void updateMyAiPreference_savesAndReturnsUpdatedPreference() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(preferenceRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(preferenceRepository.save(any(UserAiPreference.class))).thenAnswer(invocation -> {
            UserAiPreference pref = invocation.getArgument(0);
            pref.setId(10L);
            return pref;
        });

        UserAiPreferenceRequest request = UserAiPreferenceRequest.builder()
                .preferredCallName("Xưng em - gọi Thầy")
                .aiTone("CONCISE")
                .responseLength("STEP_BY_STEP")
                .customContext("Sinh viên sắp tốt nghiệp")
                .build();

        UserAiPreferenceResponse response = userAiPreferenceService.updateMyAiPreference(principal, request);

        assertThat(response.getPreferredCallName()).isEqualTo("Xưng em - gọi Thầy");
        assertThat(response.getAiTone()).isEqualTo("CONCISE");
        assertThat(response.getResponseLength()).isEqualTo("STEP_BY_STEP");
        assertThat(response.getCustomContext()).isEqualTo("Sinh viên sắp tốt nghiệp");
    }
}
