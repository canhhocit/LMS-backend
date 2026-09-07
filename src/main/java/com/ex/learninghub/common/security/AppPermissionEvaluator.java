package com.ex.learninghub.common.security;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.modules.user.service.AdminPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collections;

/**
 * Custom PermissionEvaluator để gọn kiểm tra quyền trong @PreAuthorize.
 * <p>
 * Cú pháp sử dụng: {@code @PreAuthorize("hasPermission(null, 'MANAGE_CURRICULUM')")}
 * <p>
 * Hoặc với targetId/targetType: {@code @PreAuthorize("hasPermission(#clazzId, 'CLAZZ', 'MANAGE_CLAZZ')")}
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AppPermissionEvaluator implements PermissionEvaluator {

    private final AdminPermissionService adminPermissionService;
    private final UserDetailsService userDetailsService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null) {
            log.warn("hasPermission called with null authentication");
            return false;
        }

        String permissionCode = String.valueOf(permission);
        return checkPermission(authentication, permissionCode, null, null);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId,
                                 String targetType, Object permission) {
        if (authentication == null) {
            log.warn("hasPermission called with null authentication");
            return false;
        }

        String permissionCode = String.valueOf(permission);
        return checkPermission(authentication, permissionCode, targetType, targetId);
    }

    private boolean checkPermission(Authentication authentication,
                                    String permissionCode,
                                    String targetType,
                                    Serializable targetId) {
        // Lấy user từ authentication
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal userPrincipal)) {
            log.warn("Principal is not UserPrincipal: {}", principal);
            return false;
        }

        // Nếu user có role ADMIN → kiểm tra quyền admin
        if (userPrincipal.getUser().getRole() == Role.ADMIN) {
            boolean allowed = adminPermissionService.hasPermission(authentication, permissionCode);
            log.debug("Admin permission check: user={}, permission={}, allowed={}",
                    userPrincipal.getUsername(), permissionCode, allowed);
            return allowed;
        }

        // Nếu targetType == "CLAZZ" → có thể mở rộng cho quyền giảng viên (cải tiến 1)
        if ("CLAZZ".equals(targetType) && targetId != null) {
            // TODO: gọi ClazzAuthorizationService sau khi cải tiến 1 được merge
            // Ví dụ: return clazzAuthorizationService.canManage(targetId, userPrincipal, permissionCode);
            log.debug("CLAZZ permission not yet implemented: user={}, clazzId={}, permission={}",
                    userPrincipal.getUsername(), targetId, permissionCode);
            return false;
        }

        // Mặc định: chỉ ADMIN mới được qua
        log.debug("Access denied: user={}, role={}, permission={}, targetType={}",
                userPrincipal.getUsername(), userPrincipal.getUser().getRole(),
                permissionCode, targetType);
        return false;
    }

    /**
     * Helper để lấy Authentication từ SecurityContext nếu cần dùng trong service.
     */
    public Authentication getCurrentAuthentication() {
        return org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
    }
}
