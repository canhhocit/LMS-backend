package com.ex.learninghub.modules.admin.service;

import com.ex.learninghub.modules.admin.dto.request.MentorApprovalRequest;
import com.ex.learninghub.modules.admin.dto.response.MentorRequestResponse;
import java.util.List;

public interface AdminService {
    MentorRequestResponse submitMentorRequest(String email, String bio, String experience, String skills);
    List<MentorRequestResponse> getPendingRequests();
    MentorRequestResponse processMentorRequest(Long requestId, MentorApprovalRequest request);
}
