package com.ex.learninghub.modules.admin.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.admin.dto.request.MentorApprovalRequest;
import com.ex.learninghub.modules.admin.dto.response.MentorRequestResponse;
import com.ex.learninghub.modules.admin.entity.MentorRequest;
import com.ex.learninghub.modules.admin.repository.MentorRequestRepository;
import com.ex.learninghub.modules.admin.service.AdminService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final MentorRequestRepository mentorRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MentorRequestResponse submitMentorRequest(String email, String bio, String experience, String skills) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (mentorRequestRepository.existsByUserIdAndStatus(user.getId(), "PENDING")) {
            throw new AppException(ErrorCode.MENTOR_REQUEST_PENDING);
        }

        MentorRequest mentorRequest = MentorRequest.builder()
                .user(user)
                .bio(bio)
                .experience(experience)
                .skills(skills)
                .status("PENDING")
                .build();

        mentorRequest = mentorRequestRepository.save(mentorRequest);
        return mapToResponse(mentorRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorRequestResponse> getPendingRequests() {
        return mentorRequestRepository.findByStatus("PENDING").stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MentorRequestResponse processMentorRequest(Long requestId, MentorApprovalRequest request) {
        MentorRequest mentorRequest = mentorRequestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.MENTOR_REQUEST_NOT_FOUND));

        if (!mentorRequest.getStatus().equals("PENDING")) {
            throw new AppException(ErrorCode.KEY_INVALID);
        }

        String newStatus = request.getStatus().toUpperCase();
        if (newStatus.equals("APPROVED")) {
            mentorRequest.setStatus("APPROVED");
            User user = mentorRequest.getUser();
            user.setRole(Role.MENTOR);
            userRepository.save(user);
        } else if (newStatus.equals("REJECTED")) {
            mentorRequest.setStatus("REJECTED");
            mentorRequest.setRejectionReason(request.getRejectionReason());
        } else {
            throw new AppException(ErrorCode.KEY_INVALID);
        }

        mentorRequest = mentorRequestRepository.save(mentorRequest);
        return mapToResponse(mentorRequest);
    }

    private MentorRequestResponse mapToResponse(MentorRequest mentorRequest) {
        return MentorRequestResponse.builder()
                .id(mentorRequest.getId())
                .userId(mentorRequest.getUser().getId())
                .userEmail(mentorRequest.getUser().getEmail())
                .bio(mentorRequest.getBio())
                .experience(mentorRequest.getExperience())
                .skills(mentorRequest.getSkills())
                .status(mentorRequest.getStatus())
                .rejectionReason(mentorRequest.getRejectionReason())
                .createdAt(mentorRequest.getCreatedAt())
                .build();
    }
}
