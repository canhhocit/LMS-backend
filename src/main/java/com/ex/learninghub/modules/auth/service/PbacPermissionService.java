package com.ex.learninghub.modules.auth.service;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.auth.dto.ApprovePermissionRequest;
import com.ex.learninghub.modules.auth.dto.AuditLogResponse;
import com.ex.learninghub.modules.auth.dto.PermissionRequestDto;
import com.ex.learninghub.modules.auth.entity.PermissionAuditLog;
import com.ex.learninghub.modules.auth.entity.PermissionRequest;
import com.ex.learninghub.modules.auth.repository.PermissionAuditLogRepository;
import com.ex.learninghub.modules.auth.repository.PermissionRequestRepository;
import com.ex.learninghub.common.email.EmailService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PbacPermissionService {

    private final PermissionRequestRepository requestRepository;
    private final PermissionAuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public PermissionRequestDto createRequest(Long classId, String permissionType, String reason, UserPrincipal lecturerPrincipal) {
        User lecturer = userRepository.findById(lecturerPrincipal.getUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PermissionRequest request = PermissionRequest.builder()
                .lecturer(lecturer)
                .classId(classId)
                .permissionType(permissionType != null ? permissionType : "EDIT_GRADES")
                .reason(reason)
                .status("PENDING")
                .build();

        PermissionRequest saved = requestRepository.save(request);
        log.info("Giảng viên {} đã tạo yêu cầu cấp quyền PBAC cho lớp ID {}", lecturer.getFullName(), classId);
        return mapToDto(saved);
    }

    @Transactional
    public PermissionRequestDto approveOrRejectRequest(Long requestId, ApprovePermissionRequest body, UserPrincipal adminPrincipal) {
        PermissionRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.KEY_INVALID));

        User admin = userRepository.findById(adminPrincipal.getUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        request.setApprovedBy(admin);
        if (body.isApproved()) {
            request.setStatus("APPROVED");
            LocalDateTime validUntil = LocalDateTime.now().plusMinutes(body.getDurationMinutes());
            request.setValidUntil(validUntil);

            // Audit log
            auditLogRepository.save(PermissionAuditLog.builder()
                    .action("GRANT_PERMISSION")
                    .performedBy(request.getLecturer())
                    .approvedBy(admin)
                    .classId(request.getClassId())
                    .oldValue("STATUS: PENDING")
                    .newValue("STATUS: APPROVED, VALID_UNTIL: " + validUntil)
                    .build());

            // Send Email Notification via Brevo SMTP
            try {
                emailService.sendSimpleEmail(
                        request.getLecturer().getEmail(),
                        "THÔNG BÁO CẤP QUYỀN SỬA ĐIỂM - LEARNINGHUB LMS",
                        String.format("Kính gửi Giảng viên %s,\n\nYêu cầu cấp quyền sửa điểm của thầy/cô cho Lớp ID %d đã được Admin %s ĐỒNG Ý.\nThời hạn hiệu lực: %d phút (đến %s).\n\nVui lòng hoàn tất chỉnh sửa điểm trước khi thời hạn kết thúc.",
                                request.getLecturer().getFullName(), request.getClassId(), admin.getFullName(), body.getDurationMinutes(), validUntil.toString())
                );
            } catch (Exception e) {
                log.warn("Lỗi gửi email Brevo tới giảng viên: {}", e.getMessage());
            }
        } else {
            request.setStatus("REJECTED");
            request.setValidUntil(null);
        }

        PermissionRequest updated = requestRepository.save(request);
        return mapToDto(updated);
    }

    @Transactional
    public PermissionRequestDto revokePermission(Long requestId, UserPrincipal adminPrincipal) {
        PermissionRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.KEY_INVALID));

        User admin = userRepository.findById(adminPrincipal.getUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        request.setStatus("REVOKED");
        request.setValidUntil(LocalDateTime.now());

        auditLogRepository.save(PermissionAuditLog.builder()
                .action("REVOKE_PERMISSION")
                .performedBy(request.getLecturer())
                .approvedBy(admin)
                .classId(request.getClassId())
                .oldValue("STATUS: APPROVED")
                .newValue("STATUS: REVOKED")
                .build());

        PermissionRequest updated = requestRepository.save(request);
        log.info("Admin {} đã thu hồi quyền PBAC của giảng viên ID {}", admin.getFullName(), request.getLecturer().getId());
        return mapToDto(updated);
    }

    public List<PermissionRequestDto> getLecturerRequests(UserPrincipal lecturerPrincipal) {
        return requestRepository.findByLecturerIdOrderByCreatedAtDesc(lecturerPrincipal.getUser().getId())
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<PermissionRequestDto> getAllRequests() {
        return requestRepository.findAll()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<AuditLogResponse> getAuditLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc()
                .stream().map(logItem -> AuditLogResponse.builder()
                        .id(logItem.getId())
                        .action(logItem.getAction())
                        .performedByName(logItem.getPerformedBy() != null ? logItem.getPerformedBy().getFullName() : "System")
                        .classId(logItem.getClassId())
                        .targetStudentId(logItem.getTargetStudentId())
                        .oldValue(logItem.getOldValue())
                        .newValue(logItem.getNewValue())
                        .approvedByName(logItem.getApprovedBy() != null ? logItem.getApprovedBy().getFullName() : "N/A")
                        .timestamp(logItem.getTimestamp())
                        .build()
                ).collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoExpirePermissions() {
        LocalDateTime now = LocalDateTime.now();
        List<PermissionRequest> expired = requestRepository.findByStatusAndValidUntilBefore("APPROVED", now);
        for (PermissionRequest r : expired) {
            r.setStatus("EXPIRED");
            requestRepository.save(r);
            log.info("Quyền PBAC ID {} của giảng viên {} đã tự động HẾT HẠN", r.getId(), r.getLecturer().getFullName());
        }
    }

    private PermissionRequestDto mapToDto(PermissionRequest r) {
        return PermissionRequestDto.builder()
                .id(r.getId())
                .lecturerId(r.getLecturer().getId())
                .lecturerName(r.getLecturer().getFullName())
                .permissionType(r.getPermissionType())
                .classId(r.getClassId())
                .status(r.getStatus())
                .reason(r.getReason())
                .approvedById(r.getApprovedBy() != null ? r.getApprovedBy().getId() : null)
                .approvedByName(r.getApprovedBy() != null ? r.getApprovedBy().getFullName() : null)
                .validUntil(r.getValidUntil())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
