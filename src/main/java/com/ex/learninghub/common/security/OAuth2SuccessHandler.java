package com.ex.learninghub.common.security;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.enums.UserStatus;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            log.error("OAuth2 user does not contain email attribute");
            response.sendRedirect(frontendUrl + "/login?error=email_not_found");
            return;
        }

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            log.info("Tự động tạo tài khoản sinh viên mới từ SSO OAuth2: {}", email);
            User newUser = User.builder()
                    .email(email)
                    .fullName(name != null ? name : "SSO User")
                    .password("{noop}OAUTH2_FEDERATED_USER")
                    .role(Role.STUDENT)
                    .status(UserStatus.ACTIVE)
                    .isFirstLogin(false)
                    .build();
            return userRepository.save(newUser);
        });

        String token = jwtTokenProvider.generateToken(user.getEmail());
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("token", token)
                .build().toUriString();

        log.info("OAuth2 SSO đăng nhập thành công cho {}. Chuyển hướng tới {}", email, targetUrl);
        response.sendRedirect(targetUrl);
    }
}
