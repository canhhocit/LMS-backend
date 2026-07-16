package com.ex.learninghub.modules.assessment.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.assessment.dto.request.AssignmentRequest;
import com.ex.learninghub.modules.assessment.dto.request.GradeSubmissionRequest;
import com.ex.learninghub.modules.assessment.dto.request.SubmissionRequest;
import com.ex.learninghub.modules.assessment.dto.response.AssignmentResponse;
import com.ex.learninghub.modules.assessment.dto.response.SubmissionResponse;

import java.util.List;

public interface AssessmentService {

    AssignmentResponse createAssignment(Long classId, AssignmentRequest request, UserPrincipal userPrincipal);

    AssignmentResponse updateAssignment(Long id, AssignmentRequest request, UserPrincipal userPrincipal);

    void deleteAssignment(Long id, UserPrincipal userPrincipal);

    List<AssignmentResponse> getAssignmentsByClass(Long classId);

    SubmissionResponse submitAssignment(Long assignmentId, SubmissionRequest request, UserPrincipal userPrincipal);

    SubmissionResponse gradeSubmission(Long submissionId, GradeSubmissionRequest request, UserPrincipal userPrincipal);

    List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId, UserPrincipal userPrincipal);

    List<SubmissionResponse> getMySubmissions(UserPrincipal userPrincipal);
}
