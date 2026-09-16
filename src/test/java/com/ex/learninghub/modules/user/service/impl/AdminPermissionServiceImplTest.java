package com.ex.learninghub.modules.user.service.impl;

import com.ex.learninghub.common.enums.AdminPermission;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.audit.service.AuditService;
import com.ex.learninghub.modules.user.entity.AdminPermissionEntity;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.AdminPermissionRepository;
import com.ex.learninghub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminPermissionServiceImplTest {

    @Mock
    private AdminPermissionRepository permissionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AdminPermissionServiceImpl adminPermissionService;

    private User adminUser;
    private User studentUser;
    private AdminPermissionEntity manageUsersEntity;

    @BeforeEach
    void setUp() {
        manageUsersEntity = AdminPermissionEntity.builder()
                .code(AdminPermission.MANAGE_USERS)
                .description("Manage users")
                .build();
        manageUsersEntity.setId(1L);

        adminUser = User.builder()
                .email("admin@test.com")
                .role(Role.ADMIN)
                .adminPermissions(new HashSet<>(Set.of(manageUsersEntity)))
                .build();
        adminUser.setId(100L);

        studentUser = User.builder()
                .email("student@test.com")
                .role(Role.STUDENT)
                .build();
        studentUser.setId(200L);

        SecurityContextHolder.clearContext();
    }

    @Test
    void hasPermission_returnsTrue_whenAdminHasPermission() {
        UserPrincipal principal = new UserPrincipal(adminUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        boolean hasPerm = adminPermissionService.hasPermission(auth, "MANAGE_USERS");

        assertThat(hasPerm).isTrue();
    }

    @Test
    void hasPermission_returnsFalse_whenAdminLacksPermission() {
        UserPrincipal principal = new UserPrincipal(adminUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        boolean hasPerm = adminPermissionService.hasPermission(auth, "MANAGE_TUITION");

        assertThat(hasPerm).isFalse();
    }

    @Test
    void hasPermission_returnsFalse_whenUserIsNotAdmin() {
        UserPrincipal principal = new UserPrincipal(studentUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        boolean hasPerm = adminPermissionService.hasPermission(auth, "MANAGE_USERS");

        assertThat(hasPerm).isFalse();
    }

    @Test
    void updateUserPermissions_updatesPermissionsSuccessfully() {
        UserPrincipal principal = new UserPrincipal(adminUser);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        when(userRepository.findById(100L)).thenReturn(Optional.of(adminUser));
        when(permissionRepository.findByCode(AdminPermission.MANAGE_TUITION))
                .thenReturn(Optional.of(AdminPermissionEntity.builder().code(AdminPermission.MANAGE_TUITION).description("Tuition").build()));

        adminPermissionService.updateUserPermissions(100L, List.of("MANAGE_TUITION"));

        verify(userRepository, times(1)).save(adminUser);
    }
}
