package com.ex.learninghub.modules.user.service.impl;

import com.ex.learninghub.common.enums.AdminPermission;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.security.UserPrincipal;
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
                .adminPermissions(Set.of(manageUsersEntity))
                .build();
        adminUser.setId(100L);

        studentUser = User.builder()
                .email("student@test.com")
                .role(Role.STUDENT)
                .build();
        studentUser.setId(200L);
    }

    @Test
    void hasPermission_returnsTrue_whenAdminHasPermission() {
        when(userRepository.findById(100L)).thenReturn(Optional.of(adminUser));
        Authentication auth = new UsernamePasswordAuthenticationToken(new UserPrincipal(adminUser), null);

        boolean hasPerm = adminPermissionService.hasPermission(auth, "MANAGE_USERS");

        assertThat(hasPerm).isTrue();
    }

    @Test
    void hasPermission_returnsFalse_whenAdminLacksPermission() {
        when(userRepository.findById(100L)).thenReturn(Optional.of(adminUser));
        Authentication auth = new UsernamePasswordAuthenticationToken(new UserPrincipal(adminUser), null);

        boolean hasPerm = adminPermissionService.hasPermission(auth, "MANAGE_TUITION");

        assertThat(hasPerm).isFalse();
    }

    @Test
    void hasPermission_returnsFalse_whenUserIsNotAdmin() {
        Authentication auth = new UsernamePasswordAuthenticationToken(new UserPrincipal(studentUser), null);

        boolean hasPerm = adminPermissionService.hasPermission(auth, "MANAGE_USERS");

        assertThat(hasPerm).isFalse();
    }

    @Test
    void updateUserPermissions_updatesPermissionsSuccessfully() {
        when(userRepository.findById(100L)).thenReturn(Optional.of(adminUser));
        when(permissionRepository.findByCode(AdminPermission.MANAGE_TUITION))
                .thenReturn(Optional.of(AdminPermissionEntity.builder().code(AdminPermission.MANAGE_TUITION).description("Tuition").build()));

        adminPermissionService.updateUserPermissions(100L, List.of("MANAGE_TUITION"));

        verify(userRepository, times(1)).save(adminUser);
    }
}
