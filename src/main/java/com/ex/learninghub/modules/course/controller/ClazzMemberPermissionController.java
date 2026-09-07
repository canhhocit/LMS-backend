package com.ex.learninghub.modules.course.controller;

import com.ex.learninghub.common.enums.ClazzPermissionCode;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.dto.GrantPermissionsRequest;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.entity.ClazzMemberPermission;
import com.ex.learninghub.modules.course.entity.ClazzPermission;
import com.ex.learninghub.modules.course.repository.ClazzMemberPermissionRepository;
import com.ex.learninghub.modules.course.repository.ClazzPermissionRepository;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.course.service.ClazzAuthorizationService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clazzes")
@RequiredArgsConstructor
public class ClazzMemberPermissionController {

    private final ClazzRepository clazzRepository;
    private final UserRepository userRepository;
    private final ClazzPermissionRepository clazzPermissionRepository;
    private final ClazzMemberPermissionRepository memberPermissionRepository;
    private final ClazzAuthorizationService authorizationService;

    @PostMapping("/{clazzId}/permissions")
    public ResponseEntity<Void> grantPermissions(
            @PathVariable Long clazzId,
            @RequestBody GrantPermissionsRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

        authorizationService.assertCanManage(clazz, principal, ClazzPermissionCode.MANAGE_CONTENT.name());

        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (targetUser.getRole() != Role.LECTURER) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        memberPermissionRepository.deleteByClazzIdAndUserId(clazzId, request.getUserId());

        for (String code : request.getPermissionCodes()) {
            ClazzPermission permission = clazzPermissionRepository.findByCode(code)
                    .orElseThrow(() -> new AppException(ErrorCode.KEY_INVALID));
            ClazzMemberPermission grant = ClazzMemberPermission.builder()
                    .clazz(clazz)
                    .user(targetUser)
                    .permission(permission)
                    .grantedBy(principal.getUser())
                    .build();
            memberPermissionRepository.save(grant);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{clazzId}/permissions/{userId}")
    public ResponseEntity<List<String>> getPermissions(
            @PathVariable Long clazzId,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal principal) {

        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

        authorizationService.assertCanManage(clazz, principal, ClazzPermissionCode.MANAGE_CONTENT.name());

        List<ClazzMemberPermission> grants = memberPermissionRepository.findByClazzIdAndUserId(clazzId, userId);
        List<String> permissionCodes = grants.stream()
                .map(g -> g.getPermission().getCode())
                .collect(Collectors.toList());

        return ResponseEntity.ok(permissionCodes);
    }

    @DeleteMapping("/{clazzId}/permissions/{userId}")
    public ResponseEntity<Void> revokeAllPermissions(
            @PathVariable Long clazzId,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal principal) {

        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

        authorizationService.assertCanManage(clazz, principal, ClazzPermissionCode.MANAGE_CONTENT.name());

        memberPermissionRepository.deleteByClazzIdAndUserId(clazzId, userId);
        return ResponseEntity.noContent().build();
    }
}