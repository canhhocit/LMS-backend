package com.ex.learninghub.modules.auth.dto.response;

import com.ex.learninghub.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    
    @Builder.Default
    private String type = "Bearer";
    
    private Long id;
    private String email;
    private String fullName;
    private Role role;
    private boolean isFirstLogin;

    @Builder.Default
    private String refreshToken = null;
}
