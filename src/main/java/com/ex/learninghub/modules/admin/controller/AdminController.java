package com.ex.learninghub.modules.admin.controller;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.repository.CourseRepository;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.mentor.repository.MentorRequestRepository;
import com.ex.learninghub.modules.mentor.service.MentorRequestService;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final MentorRequestRepository mentorRequestRepository;
    private final MentorRequestService mentorRequestService;

    @GetMapping("/dashboard")
    public DashboardDTO getDashboard() {
        long totalUsers = userRepository.count();
        long totalCourses = courseRepository.count();
        long totalEnrollments = enrollmentRepository.count();
        
        // Count pending mentor requests using repository
        var pendingRequests = mentorRequestRepository.findAll().stream()
            .filter(r -> r.getStatus() == com.ex.learninghub.common.enums.MentorRequestStatus.PENDING)
            .count();
        
        return DashboardDTO.builder()
                .totalUsers(totalUsers)
                .totalCourses(totalCourses)
                .totalEnrollments(totalEnrollments)
                .pendingMentorRequests(pendingRequests)
                .build();
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Return all users for admin - in production would use pagination
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/courses")
    public ResponseEntity<?> getAllCourses(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    @PatchMapping("/courses/{id}/status")
    public ResponseEntity<?> updateCourseStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        
        try {
            course.setStatus(com.ex.learninghub.common.enums.CourseStatus.valueOf(status.toUpperCase()));
            courseRepository.save(course);
            return ResponseEntity.ok(Map.of("message", "Course status updated successfully"));
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.KEY_INVALID);
        }
    }

    @GetMapping("/mentor-requests")
    public ResponseEntity<?> getAllMentorRequests(
            @RequestParam(required = false) String status) {
        if (status != null) {
            var statusEnum = com.ex.learninghub.common.enums.MentorRequestStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(mentorRequestRepository.findByStatus(statusEnum));
        }
        return ResponseEntity.ok(mentorRequestRepository.findAll());
    }

    @PatchMapping("/mentor-requests/{id}/approve")
    public ResponseEntity<?> approveMentorRequest(@PathVariable Long id) {
        mentorRequestService.approveRequest(id);
        return ResponseEntity.ok(Map.of("message", "Mentor request approved"));
    }

    @PatchMapping("/mentor-requests/{id}/reject")
    public ResponseEntity<?> rejectMentorRequest(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String reason = request.get("rejectionReason");
        mentorRequestService.rejectRequest(id, reason);
        return ResponseEntity.ok(Map.of("message", "Mentor request rejected"));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardDTO {
        private long totalUsers;
        private long totalCourses;
        private long totalEnrollments;
        private long pendingMentorRequests;
    }
}
