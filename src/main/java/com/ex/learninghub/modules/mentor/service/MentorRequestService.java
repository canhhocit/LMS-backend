package com.ex.learninghub.modules.mentor.service;

import com.ex.learninghub.modules.mentor.dto.CreateMentorRequestDTO;
import com.ex.learninghub.modules.mentor.dto.MentorRequestDTO;
import com.ex.learninghub.common.enums.MentorRequestStatus;

import java.util.List;

public interface MentorRequestService {
    
    MentorRequestDTO createRequest(Long userId, CreateMentorRequestDTO dto);
    
    List<MentorRequestDTO> getAllRequests(MentorRequestStatus status);
    
    MentorRequestDTO getRequestById(Long id);
    
    MentorRequestDTO getMyRequest(Long userId);
    
    MentorRequestDTO approveRequest(Long id);
    
    MentorRequestDTO rejectRequest(Long id, String rejectionReason);
}