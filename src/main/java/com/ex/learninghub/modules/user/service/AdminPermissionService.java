package com.ex.learninghub.modules.user.service;

import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Map;

public interface AdminPermissionService {
    boolean hasPermission(Authentication authentication, String permissionCode);
    List<Map<String, String>> getAllPermissions();
    List<String> getUserPermissions(Long userId);
    void updateUserPermissions(Long userId, List<String> permissionCodes);
}
