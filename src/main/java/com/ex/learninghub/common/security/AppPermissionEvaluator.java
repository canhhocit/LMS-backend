package com.ex.learninghub.common.security;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.modules.user.service.AdminPermissionService;
import com.ex.learninghub.modules.course.service.ClazzAuthorizationService;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.course.entity.Clazz;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import java.io.Serializable;

/**
 * Custom PermissionEvaluator để kiểm tra quyền trong các biểu thức @PreAuthorize.
 * Hỗ trợ quyền ADMIN và quyền CLAZZ thông qua ClazzAuthorizationService.
 */
@Component
@Slf4j
public class AppPermissionEvaluator implements PermissionEvaluator {

    private final AdminPermissionService adminPermissionService;
    private final ClazzAuthorizationService clazzAuthorizationService;
    private final ClazzRepository clazzRepository;

    public AppPermissionEvaluator(@Lazy AdminPermissionService adminPermissionService,
                                  @Lazy ClazzAuthorizationService clazzAuthorizationService,
                                  @Lazy ClazzRepository clazzRepository) {
        this.adminPermissionService = adminPermissionService;
        this.clazzAuthorizationService = clazzAuthorizationService;
        this.clazzRepository = clazzRepository;
    }

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
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal userPrincipal)) {
            log.warn("Principal is not UserPrincipal: {}", principal);
            return false;
        }
        // ADMIN luôn cho phép
        if (userPrincipal.getUser().getRole() == Role.ADMIN) {
            boolean allowed = adminPermissionService.hasPermission(authentication, permissionCode);
            log.debug("Admin permission check: user={}, permission={}, allowed={}",
                    userPrincipal.getUsername(), permissionCode, allowed);
            return allowed;
        }
        // CLAZZ permission
        if ("CLAZZ".equals(targetType) && targetId != null) {
            Long clazzId = Long.valueOf(targetId.toString());
            Clazz clazz = clazzRepository.findById(clazzId)
                    .orElseThrow(() -> new RuntimeException("Clazz not found"));
            boolean allowed = clazzAuthorizationService.canManage(clazz, userPrincipal, permissionCode);
            log.debug("CLAZZ permission check: user={}, clazzId={}, permission={}, allowed={}",
                    userPrincipal.getUsername(), clazzId, permissionCode, allowed);
            return allowed;
        }
        log.debug("Access denied: user={}, role={}, permission={}, targetType={}",
                userPrincipal.getUsername(), userPrincipal.getUser().getRole(), permissionCode, targetType);
        return false;
    }
}
