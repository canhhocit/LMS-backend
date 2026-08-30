package com.ex.learninghub.modules.user.service.impl;

import com.ex.learninghub.common.enums.AdminPermission;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.modules.user.dto.request.UserCreateRequest;
import com.ex.learninghub.modules.user.entity.AdministrativeClass;
import com.ex.learninghub.modules.user.entity.AdminPermissionEntity;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.AdministrativeClassRepository;
import com.ex.learninghub.modules.user.repository.AdminPermissionRepository;
import com.ex.learninghub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AdministrativeClassRepository adminClassRepository;

    @Mock
    private com.ex.learninghub.modules.curriculum.repository.CurriculumRepository curriculumRepository;

    @Mock
    private AdminPermissionRepository adminPermissionRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_whenAdmin_assignsDefaultPermissions() {
        UserCreateRequest request = new UserCreateRequest();
        request.setFullName("Admin Tester");
        request.setEmail("admin@test.com");
        request.setRole(Role.ADMIN);

        when(userRepository.existsByEmail("admin@test.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded-password");
        when(adminPermissionRepository.findByCode(AdminPermission.VIEW_REPORTS))
                .thenReturn(Optional.of(AdminPermissionEntity.builder().code(AdminPermission.VIEW_REPORTS).description("View reports").build()));
        when(adminPermissionRepository.findByCode(AdminPermission.MANAGE_USERS))
                .thenReturn(Optional.of(AdminPermissionEntity.builder().code(AdminPermission.MANAGE_USERS).description("Manage users").build()));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        User saved = userService.createUser(request);

        assertThat(saved.getAdminPermissions())
                .extracting("code")
                .containsExactlyInAnyOrder(AdminPermission.VIEW_REPORTS, AdminPermission.MANAGE_USERS);
        assertThat(saved.getRole()).isEqualTo(Role.ADMIN);
    }
}
