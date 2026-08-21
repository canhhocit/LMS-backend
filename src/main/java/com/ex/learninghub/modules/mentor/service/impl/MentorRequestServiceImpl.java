package com.ex.learninghub.modules.mentor.service.impl;

import com.ex.learninghub.common.enums.MentorRequestStatus;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.mentor.dto.CreateMentorRequestDTO;
import com.ex.learninghub.modules.mentor.dto.MentorRequestDTO;
import com.ex.learninghub.modules.mentor.entity.MentorRequest;
import com.ex.learninghub.modules.mentor.mapper.MentorRequestMapper;
import com.ex.learninghub.modules.mentor.repository.MentorRequestRepository;
import com.ex.learninghub.modules.mentor.service.MentorRequestService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorRequestServiceImpl implements MentorRequestService {
    
    private final MentorRequestRepository mentorRequestRepository;
    private final UserRepository userRepository;
    private final MentorRequestMapper mentorRequestMapper;
    
    @Override
    @Transactional
    public MentorRequestDTO createRequest(Long userId, CreateMentorRequestDTO dto) {
        // Check if user already has a pending request
        if (mentorRequestRepository.existsByUserIdAndStatus(userId, MentorRequestStatus.PENDING)) {
            throw new AppException(ErrorCode.MENTOR_REQUEST_ALREADY_EXISTS);
        }
        
        // Check if user is already a mentor
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        if (user.getRole() == Role.MENTOR) {
            throw new AppException(ErrorCode.MENTOR_REQUEST_ALREADY_EXISTS);
        }
        
        // Create new mentor request
        MentorRequest request = MentorRequest.builder()
                .userId(userId)
                .bio(dto.getBio())
                .experience(dto.getExperience())
                .skills(dto.getSkills())
                .status(MentorRequestStatus.PENDING)
                .build();
        
        request = mentorRequestRepository.save(request);
        return mentorRequestMapper.toDTO(request, user);
    }
    
    @Override
    public List<MentorRequestDTO> getAllRequests(MentorRequestStatus status) {
        List<MentorRequest> requests;
        if (status != null) {
            requests = mentorRequestRepository.findByStatus(status);
        } else {
            requests = mentorRequestRepository.findAll();
        }
        
        return requests.stream()
                .map(req -> {
                    User user = userRepository.findById(req.getUserId()).orElse(null);
                    return mentorRequestMapper.toDTO(req, user);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public MentorRequestDTO getRequestById(Long id) {
        MentorRequest request = mentorRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MENTOR_REQUEST_NOT_FOUND));
        
        User user = userRepository.findById(request.getUserId()).orElse(null);
        return mentorRequestMapper.toDTO(request, user);
    }
    
    @Override
    public MentorRequestDTO getMyRequest(Long userId) {
        MentorRequest request = mentorRequestRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.MENTOR_REQUEST_NOT_FOUND));
        
        User user = userRepository.findById(request.getUserId()).orElse(null);
        return mentorRequestMapper.toDTO(request, user);
    }
    
    @Override
    @Transactional
    public MentorRequestDTO approveRequest(Long id) {
        MentorRequest request = mentorRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MENTOR_REQUEST_NOT_FOUND));
        
        if (request.getStatus() != MentorRequestStatus.PENDING) {
            throw new AppException(ErrorCode.MENTOR_REQUEST_ALREADY_PROCESSED);
        }
        
        // Update request status
        request.setStatus(MentorRequestStatus.APPROVED);
        request = mentorRequestRepository.save(request);
        
        // Update user role to MENTOR
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setRole(Role.MENTOR);
        userRepository.save(user);
        
        return mentorRequestMapper.toDTO(request, user);
    }
    
    @Override
    @Transactional
    public MentorRequestDTO rejectRequest(Long id, String rejectionReason) {
        MentorRequest request = mentorRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MENTOR_REQUEST_NOT_FOUND));
        
        if (request.getStatus() != MentorRequestStatus.PENDING) {
            throw new AppException(ErrorCode.MENTOR_REQUEST_ALREADY_PROCESSED);
        }
        
        // Update request status
        request.setStatus(MentorRequestStatus.REJECTED);
        request.setRejectionReason(rejectionReason);
        request = mentorRequestRepository.save(request);
        
        User user = userRepository.findById(request.getUserId()).orElse(null);
        return mentorRequestMapper.toDTO(request, user);
    }
}