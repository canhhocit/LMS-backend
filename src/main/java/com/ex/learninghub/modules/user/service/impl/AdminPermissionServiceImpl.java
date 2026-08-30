package com.ex.learninghub.modules.user.service.impl;

import com.ex.learninghub.common.enums.AdminPermission;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.user.entity.AdminPermissionEntity;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.AdminPermissionRepository;
import com.ex.learninghub.modules.user.repository.UserRepository;
import com.ex.learninghub.modules.user.service.AdminPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service("adminPermissionService")
@RequiredArgsConstructor
public class AdminPermissionServiceImpl implements AdminPermissionService {

    private final AdminPermissionRepository permissionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Authentication authentication, String permissionCodeStr) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return false;
        }
        User current = principal.getUser();
        if (current == null || current.getRole() != Role.ADMIN) {
            return false;
        }

        // Fetch fresh user from DB if adminPermissions proxy needs initialization
        User user = userRepository.findById(current.getId()).orElse(current);
        if (user.getAdminPermissions() == null || user.getAdminPermissions().isEmpty()) {
            return false;
        }

        return user.getAdminPermissions().stream()
                .anyMatch(p -> p.getCode() != null && p.getCode().name().equalsIgnoreCase(permissionCodeStr));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, String>> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(p -> {
                    Map<String, String> item = new HashMap<>();
                    item.put("code", p.getCode().name());
                    item.put("description", p.getDescription());
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getUserPermissions(Long userId) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (target.getAdminPermissions() == null) {
            return List.of();
        }
        return target.getAdminPermissions().stream()
                .map(p -> p.getCode().name())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateUserPermissions(Long userId, List<String> permissionCodes) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (target.getRole() != Role.ADMIN) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        Set<AdminPermissionEntity> newPermissions = new HashSet<>();
        if (permissionCodes != null) {
            for (String codeStr : permissionCodes) {
                try {
                    AdminPermission permEnum = AdminPermission.valueOf(codeStr);
                    permissionRepository.findByCode(permEnum).ifPresent(newPermissions::add);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        target.setAdminPermissions(newPermissions);
        userRepository.save(target);
    }
}
