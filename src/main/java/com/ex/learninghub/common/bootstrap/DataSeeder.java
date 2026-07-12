package com.ex.learninghub.common.bootstrap;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.default-email:admin@university.edu.vn}")
    private String adminEmail;

    @Value("${app.admin.default-password:123456}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!userRepository.existsByRole(Role.ADMIN)) {
            log.info("No Admin account found. Generating default Admin account...");
            
            User admin = User.builder()
                    .fullName("System Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .isFirstLogin(false)
                    .build();
            
            userRepository.save(admin);
            
            log.info("Default Admin account created. Email: {}, Password: {}", adminEmail, adminPassword);
        } else {
            log.info("Admin account already exists. Skipping default Admin creation.");
        }
    }
}
