package com.ex.learninghub.modules.mentor.mapper;

import com.ex.learninghub.modules.mentor.dto.MentorRequestDTO;
import com.ex.learninghub.modules.mentor.entity.MentorRequest;
import com.ex.learninghub.modules.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class MentorRequestMapper {
    
    public MentorRequestDTO toDTO(MentorRequest entity, User user) {
        if (entity == null) return null;
        
        return MentorRequestDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .userEmail(user != null ? user.getEmail() : null)
                .userFullName(user != null ? user.getFullName() : null)
                .bio(entity.getBio())
                .experience(entity.getExperience())
                .skills(entity.getSkills())
                .status(entity.getStatus())
                .rejectionReason(entity.getRejectionReason())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    public MentorRequest toEntity(MentorRequestDTO dto) {
        if (dto == null) return null;
        
        return MentorRequest.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .bio(dto.getBio())
                .experience(dto.getExperience())
                .skills(dto.getSkills())
                .status(dto.getStatus())
                .rejectionReason(dto.getRejectionReason())
                .build();
    }
}