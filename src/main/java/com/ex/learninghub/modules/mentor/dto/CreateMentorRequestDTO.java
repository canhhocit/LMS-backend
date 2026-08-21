package com.ex.learninghub.modules.mentor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMentorRequestDTO {
    
    @NotBlank(message = "BIO_REQUIRED")
    private String bio;
    
    @NotBlank(message = "EXPERIENCE_REQUIRED")
    private String experience;
    
    @NotBlank(message = "SKILLS_REQUIRED")
    private String skills;
}