package com.ex.learninghub.modules.course.service;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzMemberPermissionRepository;
import com.ex.learninghub.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClazzAuthorizationService {

    private final ClazzMemberPermissionRepository memberPermissionRepository;

    /**
     * Checks if the given user has the specified permission within the class context.
     *
     * @param clazz          the class (course section) to check permissions for
     * @param principal      the authenticated user principal
     * @param permissionCode the permission code to check (e.g., MANAGE_CONTENT)
     * @return true if the user has the permission, false otherwise
     */
    public boolean canManage(Clazz clazz, UserPrincipal principal, String permissionCode) {
        if (principal == null) {
            return false;
        }
        User user = principal.getUser();
        if (user == null) {
            return false;
        }

        // Admin always has full access
        if (user.getRole() == Role.ADMIN) {
            return true;
        }

        // Class lecturer has full access
        if (clazz.getLecturer() != null && clazz.getLecturer().getId().equals(user.getId())) {
            return true;
        }

        // Check for delegated permission
        return memberPermissionRepository.existsByClazzIdAndUserIdAndPermission_Code(
                clazz.getId(), user.getId(), permissionCode);
    }

    /**
     * Asserts that the given user has the specified permission within the class context.
     * Throws an AppException with FORBIDDEN error code if permission is denied.
     *
     * @param clazz          the class (course section) to check permissions for
     * @param principal      the authenticated user principal
     * @param permissionCode the permission code to check
     * @throws AppException if the user does not have the required permission
     */
    public void assertCanManage(Clazz clazz, UserPrincipal principal, String permissionCode) {
        if (!canManage(clazz, principal, permissionCode)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }
}