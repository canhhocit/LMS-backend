package com.ex.learninghub.modules.assessment.service.impl;

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
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final ClazzRepository clazzRepository;

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
        return AssignmentResponse.from(assignmentRepository.save(assignment));
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
        return AssignmentResponse.from(assignmentRepository.save(assignment));
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
        boolean late = assignment.getDueDate() != null && now.isAfter(assignment.getDueDate());
        Submission submission = Submission.builder()
                .assignment(assignment)
                .student(student)
                .fileUrl(request.getFileUrl())
                .submittedAt(now)
                .isLate(late)
                .build();
        return SubmissionResponse.from(submissionRepository.save(submission));
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
