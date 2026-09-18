package com.ex.learninghub.modules.auth.controller;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.auth.dto.ApprovePermissionRequest;
import com.ex.learninghub.modules.auth.dto.AuditLogResponse;
import com.ex.learninghub.modules.auth.dto.MenuItemResponse;
import com.ex.learninghub.modules.auth.dto.PermissionRequestDto;
import com.ex.learninghub.modules.auth.service.DynamicMenuService;
import com.ex.learninghub.modules.auth.service.PbacPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PermissionRequestController {

    private final PbacPermissionService pbacPermissionService;
    private final DynamicMenuService dynamicMenuService;

    @PostMapping("/auth/pbac/request")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<PermissionRequestDto> createRequest(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long classId = Long.parseLong(body.get("classId").toString());
        String type = body.getOrDefault("permissionType", "EDIT_GRADES").toString();
        String reason = body.getOrDefault("reason", "").toString();

        PermissionRequestDto res = pbacPermissionService.createRequest(classId, type, reason, principal);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/auth/pbac/my-requests")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<List<PermissionRequestDto>> getMyRequests(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(pbacPermissionService.getLecturerRequests(principal));
    }

    @GetMapping("/auth/pbac/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PermissionRequestDto>> getAllRequests() {
        return ResponseEntity.ok(pbacPermissionService.getAllRequests());
    }

    @PostMapping("/auth/pbac/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PermissionRequestDto> approveOrReject(
            @PathVariable Long id,
            @RequestBody ApprovePermissionRequest body,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(pbacPermissionService.approveOrRejectRequest(id, body, principal));
    }

    @PostMapping("/auth/pbac/{id}/revoke")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PermissionRequestDto> revoke(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(pbacPermissionService.revokePermission(id, principal));
    }

    @GetMapping("/auth/pbac/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogs() {
        return ResponseEntity.ok(pbacPermissionService.getAuditLogs());
    }

    @GetMapping("/users/me/menu")
    public ResponseEntity<List<MenuItemResponse>> getUserMenu(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(dynamicMenuService.getUserMenu(principal));
    }
}
