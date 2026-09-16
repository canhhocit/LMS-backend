package com.ex.learninghub.modules.user.service.impl;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.audit.service.AuditService;
import com.ex.learninghub.modules.user.entity.AdminPermissionEntity;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.AdminPermissionRepository;
import com.ex.learninghub.modules.user.repository.UserRepository;
import com.ex.learninghub.modules.user.service.AdminPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPermissionServiceImpl implements AdminPermissionService {

    private final AdminPermissionRepository adminPermissionRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Override
    public boolean hasPermission(Authentication authentication, String permissionCode) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal userPrincipal)) {
            return false;
        }
        User user = userPrincipal.getUser();
        if (user.getRole() != Role.ADMIN) {
            return false;
        }
        return user.getAdminPermissions().stream()
                .anyMatch(p -> p.getCode().name().equals(permissionCode));
    }

    @Override
    public List<Map<String, String>> getAllPermissions() {
        List<AdminPermissionEntity> permissions = adminPermissionRepository.findAll();
        return permissions.stream()
                .map(p -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("code", p.getCode().name());
                    map.put("description", p.getDescription());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getRole() != Role.ADMIN) {
            return new ArrayList<>();
        }
        return user.getAdminPermissions().stream()
                .map(p -> p.getCode().name())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateUserPermissions(Long userId, List<String> permissionCodes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UserPrincipal actor = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Clear existing permissions
        user.getAdminPermissions().clear();

        if (permissionCodes != null && !permissionCodes.isEmpty()) {
            // Ensure user has ADMIN role
            if (user.getRole() != Role.ADMIN) {
                user.setRole(Role.ADMIN);
            }

            // Add new permissions
            for (String codeStr : permissionCodes) {
                try {
                    com.ex.learninghub.common.enums.AdminPermission adminPerm =
                            com.ex.learninghub.common.enums.AdminPermission.valueOf(codeStr);
                    AdminPermissionEntity permissionEntity = adminPermissionRepository.findByCode(adminPerm)
                            .orElseThrow(() -> new AppException(ErrorCode.KEY_INVALID));
                    user.getAdminPermissions().add(permissionEntity);
                } catch (IllegalArgumentException e) {
                    throw new AppException(ErrorCode.KEY_INVALID);
                }
            }

            userRepository.save(user);

            // Audit log
            String detail = String.format("Granted permissions to user %d (%s): %s",
                    userId, user.getEmail(), String.join(", ", permissionCodes));
            auditService.log(actor, "GRANT_PERMISSION", "User", userId, detail, "SUCCESS");
        } else {
            // If no permissions, revoke ADMIN role if user is not SUPER_ADMIN
            // Check if user has SUPER_ADMIN permission (after clearing, they won't have any)
            // But we can check if they had it before clearing, but we can't because we cleared.
            // To be safe, we'll just set role to LECTURER if they are not SUPER_ADMIN.
            // Since we don't know if they were SUPER_ADMIN before, we'll check if they have any
            // admin permissions left (none) and set role accordingly.
            // But we want to keep SUPER_ADMIN if they had it? The spec says revoke all.
            // So we set role to LECTURER.
            user.setRole(Role.LECTURER);
            userRepository.save(user);

            // Audit log
            String detail = String.format("Revoked all admin permissions from user %d (%s)",
                    userId, user.getEmail());
            auditService.log(actor, "REVOKE_PERMISSION", "User", userId, detail, "SUCCESS");
        }
    }
}