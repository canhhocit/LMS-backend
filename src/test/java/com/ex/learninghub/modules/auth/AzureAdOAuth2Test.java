package com.ex.learninghub.modules.auth;

import com.ex.learninghub.modules.auth.service.AzureAdOAuth2ProviderService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AzureAdOAuth2Test {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AzureAdOAuth2ProviderService azureAdOAuth2ProviderService;

    @Test
    @DisplayName("Nên tự động tạo tài khoản sinh viên mới từ Microsoft 365 Token")
    void processAzureAdLogin_NewUser() {
        Map<String, Object> attributes = Map.of(
                "userPrincipalName", "student@university.edu.vn",
                "displayName", "Nguyễn Văn MS365"
        );

        when(userRepository.findByEmail("student@university.edu.vn")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User user = azureAdOAuth2ProviderService.processAzureAdLogin(attributes);

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("student@university.edu.vn");
        assertThat(user.getRole()).isEqualTo(com.ex.learninghub.common.enums.Role.STUDENT);
    }
}
