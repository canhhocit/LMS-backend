package com.ex.learninghub.modules.assessment.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ex.learninghub.common.enums.SubmissionType;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.assessment.dto.request.AssignmentRequest;
import com.ex.learninghub.modules.assessment.dto.request.GradeSubmissionRequest;
import com.ex.learninghub.modules.assessment.dto.request.SubmissionRequest;
import com.ex.learninghub.modules.assessment.dto.response.AssignmentResponse;
import com.ex.learninghub.modules.assessment.dto.response.SubmissionResponse;
import com.ex.learninghub.modules.assessment.entity.Assignment;
import com.ex.learninghub.modules.assessment.entity.Submission;
import com.ex.learninghub.modules.assessment.repository.AssignmentRepository;
import com.ex.learninghub.modules.assessment.repository.SubmissionRepository;
import com.ex.learninghub.modules.assessment.service.AssessmentService;
import com.ex.learninghub.common.enums.NotificationType;
import com.ex.learninghub.modules.notification.service.NotificationService;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final ClazzRepository clazzRepository;
    private final NotificationService notificationService;
    private final Cloudinary cloudinary;

    @Value("${app.upload.max-assignment-file-size:50MB}")
    private DataSize maxAssignmentFileSize;

    private static final Set<String> ALLOWED_SUBMISSION_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain",
            "application/zip",
            "application/x-zip-compressed",
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/jpg"
    );

    private void verifyLecturerOwnsClazz(Clazz clazz, UserPrincipal userPrincipal) {
        if (clazz.getLecturer() == null ||
                !clazz.getLecturer().getId().equals(userPrincipal.getUser().getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    @Override
    @Transactional
    public AssignmentResponse createAssignment(Long classId, AssignmentRequest request, UserPrincipal userPrincipal) {
        Clazz clazz = clazzRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, userPrincipal);
        Assignment assignment = Assignment.builder()
                .clazz(clazz)
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .maxScore(request.getMaxScore())
                .build();
        var saved = assignmentRepository.save(assignment);

        // Notify all enrolled students about the new assignment (WebSocket + DB)
        notificationService.notifyClazz(classId, NotificationType.NEW_ASSIGNMENT,
                "New assignment: " + request.getTitle(),
                "An assignment has been posted. Due: " + request.getDueDate(),
                saved.getId());

        return AssignmentResponse.from(saved);
    }

    @Override
    @Transactional
    public AssignmentResponse updateAssignment(Long id, AssignmentRequest request, UserPrincipal userPrincipal) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        verifyLecturerOwnsClazz(assignment.getClazz(), userPrincipal);
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate());
        assignment.setMaxScore(request.getMaxScore());
        var saved = assignmentRepository.save(assignment);

        return AssignmentResponse.from(saved);
    }

    @Override
    @Transactional
    public void deleteAssignment(Long id, UserPrincipal userPrincipal) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        verifyLecturerOwnsClazz(assignment.getClazz(), userPrincipal);
        assignmentRepository.delete(assignment);
    }

    @Override
    public List<AssignmentResponse> getAssignmentsByClass(Long classId) {
        return assignmentRepository.findByClazzId(classId).stream()
                .map(AssignmentResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubmissionResponse submitAssignment(Long assignmentId, SubmissionRequest request, UserPrincipal userPrincipal) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        User student = userPrincipal.getUser();
        if (submissionRepository.findByAssignmentIdAndStudentId(assignmentId, student.getId()).isPresent()) {
            throw new AppException(ErrorCode.SUBMISSION_EXISTS);
        }
        LocalDateTime now = LocalDateTime.now();
        SubmissionType submissionType = request.getSubmissionType() != null ? request.getSubmissionType() : SubmissionType.FILE;
        String fileUrl = request.getFileUrl();
        String fileUrls = request.getFileUrls() == null || request.getFileUrls().isEmpty() ? null : String.join(",", request.getFileUrls());
        String externalLink = request.getExternalLink();

        if (submissionType == SubmissionType.FILE || submissionType == SubmissionType.IMAGE) {
            fileUrl = request.getFileUrl() != null ? request.getFileUrl() : (fileUrls != null ? fileUrls.split(",")[0] : null);
            externalLink = null;
        } else {
            fileUrl = null;
            fileUrls = null;
        }

        if (submissionType == SubmissionType.GOOGLE_DRIVE_LINK && (externalLink == null || !externalLink.matches("(?i)^https?:\\/\\/.*drive\\.google\\.com\\/.*$"))) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        if (submissionType == SubmissionType.GITHUB_LINK && (externalLink == null || !externalLink.matches("(?i)^https?:\\/\\/github\\.com\\/.*$"))) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        if ((submissionType == SubmissionType.FILE || submissionType == SubmissionType.IMAGE) && (fileUrl == null || fileUrl.isBlank()) && (fileUrls == null || fileUrls.isBlank())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        boolean late = assignment.getDueDate() != null && now.isAfter(assignment.getDueDate());
        Submission submission = Submission.builder()
                .assignment(assignment)
                .student(student)
                .submissionType(submissionType)
                .fileUrl(fileUrl)
                .fileUrls(fileUrls)
                .externalLink(externalLink)
                .submittedAt(now)
                .isLate(late)
                .build();
        return SubmissionResponse.from(submissionRepository.save(submission));
    }

    @Override
    @Transactional
    public String uploadSubmissionFile(Long assignmentId, MultipartFile file, UserPrincipal userPrincipal) {
        return uploadSubmissionFiles(assignmentId, List.of(file), userPrincipal).get(0);
    }

    @Override
    @Transactional
    public List<String> uploadSubmissionFiles(Long assignmentId, List<MultipartFile> files, UserPrincipal userPrincipal) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        if (files == null || files.isEmpty()) {
            throw new AppException(ErrorCode.SUBMISSION_FILE_EMPTY);
        }

        return files.stream().filter(file -> file != null && !file.isEmpty()).map(file -> {
            if (file.getSize() > maxAssignmentFileSize.toBytes()) {
                throw new AppException(ErrorCode.SUBMISSION_FILE_TOO_LARGE);
            }
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_SUBMISSION_TYPES.contains(contentType)) {
                throw new AppException(ErrorCode.SUBMISSION_INVALID_FORMAT);
            }
            try {
                String resourceType = contentType.startsWith("image/") ? "image" : "raw";
                Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", resourceType));
                return (String) result.get("secure_url");
            } catch (IOException ex) {
                throw new AppException(ErrorCode.SUBMISSION_UPLOAD_FAILED);
            }
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubmissionResponse gradeSubmission(Long submissionId, GradeSubmissionRequest request, UserPrincipal userPrincipal) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        verifyLecturerOwnsClazz(submission.getAssignment().getClazz(), userPrincipal);

        BigDecimal maxScore = submission.getAssignment().getMaxScore();
        if (maxScore != null && request.getScore() != null
                && request.getScore().compareTo(maxScore) > 0) {
            throw new AppException(ErrorCode.SCORE_EXCEEDS_MAX);
        }

        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        return SubmissionResponse.from(submissionRepository.save(submission));
    }

    @Override
    public List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId, UserPrincipal userPrincipal) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        verifyLecturerOwnsClazz(assignment.getClazz(), userPrincipal);
        return submissionRepository.findByAssignmentId(assignmentId).stream()
                .map(SubmissionResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubmissionResponse> getMySubmissions(UserPrincipal userPrincipal) {
        return submissionRepository.findByStudentId(userPrincipal.getUser().getId()).stream()
                .map(SubmissionResponse::from)
                .collect(Collectors.toList());
    }
}
