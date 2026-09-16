package com.ex.learninghub.modules.auth.service;

import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureAdOAuth2ProviderService {

    private final UserRepository userRepository;

    @Data
    @Builder
    public static class AzureUserAttributes {
        private String userPrincipalName;
        private String displayName;
        private String mail;
        private String tenantId;
    }

    public User processAzureAdLogin(Map<String, Object> attributes) {
        String email = (String) attributes.getOrDefault("mail", attributes.get("userPrincipalName"));
        String name = (String) attributes.getOrDefault("displayName", "Microsoft User");

        if (email == null) {
            throw new IllegalArgumentException("Không tìm thấy thuộc tính Email từ Microsoft 365 SSO Token");
        }

        log.info("Xử lý đăng nhập Microsoft 365 Azure AD SSO cho email: {}", email);

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        // Auto-provision new Microsoft 365 student account
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFullName(name);
        newUser.setPassword("$2a$10$dummyPasswordHashForSSO");
        newUser.setRole(com.ex.learninghub.common.enums.Role.STUDENT);
        newUser.setStatus(com.ex.learninghub.common.enums.UserStatus.ACTIVE);

        return userRepository.save(newUser);
    }
}
