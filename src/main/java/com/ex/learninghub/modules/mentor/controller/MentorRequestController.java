package com.ex.learninghub.modules.mentor.controller;

import com.ex.learninghub.common.enums.MentorRequestStatus;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.mentor.dto.CreateMentorRequestDTO;
import com.ex.learninghub.modules.mentor.dto.MentorRequestDTO;
import com.ex.learninghub.modules.mentor.service.MentorRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mentor-requests")
@RequiredArgsConstructor
public class MentorRequestController {
    
    private final MentorRequestService mentorRequestService;
    
    /**
     * Learner gửi đơn xin làm mentor
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('LEARNER', 'STUDENT')")
    public ResponseEntity<MentorRequestDTO> createRequest(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateMentorRequestDTO dto) {
        MentorRequestDTO result = mentorRequestService.createRequest(principal.getUser().getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    /**
     * Learner xem đơn của chính mình
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MentorRequestDTO> getMyRequest(
            @AuthenticationPrincipal UserPrincipal principal) {
        try {
            MentorRequestDTO result = mentorRequestService.getMyRequest(principal.getUser().getId());
            return ResponseEntity.ok(result);
        } catch (AppException e) {
            if (e.getErrorCode() == ErrorCode.MENTOR_REQUEST_NOT_FOUND) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }
    
    /**
     * Admin xem danh sách đơn (lọc theo status)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MentorRequestDTO>> getAllRequests(
            @RequestParam(required = false) MentorRequestStatus status) {
        List<MentorRequestDTO> result = mentorRequestService.getAllRequests(status);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Admin xem chi tiết đơn
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MentorRequestDTO> getRequestById(@PathVariable Long id) {
        MentorRequestDTO result = mentorRequestService.getRequestById(id);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Admin duyệt đơn
     */
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MentorRequestDTO> approveRequest(@PathVariable Long id) {
        MentorRequestDTO result = mentorRequestService.approveRequest(id);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Admin từ chối đơn
     */
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MentorRequestDTO> rejectRequest(
            @PathVariable Long id,
            @RequestBody RejectRequestDTO dto) {
        MentorRequestDTO result = mentorRequestService.rejectRequest(id, dto.getReason());
        return ResponseEntity.ok(result);
    }
    
    /**
     * DTO for reject request
     */
    public static class RejectRequestDTO {
        private String reason;
        
        public String getReason() {
            return reason;
        }
        
        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}