package com.ex.learninghub.common.security;

import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private OAuth2User oAuth2User;

    @InjectMocks
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(oAuth2SuccessHandler, "frontendUrl", "http://localhost:3000");
    }

    @Test
    void onAuthenticationSuccess_existingUser_generatesTokenAndRedirects() throws Exception {
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("student@university.edu.vn");
        when(oAuth2User.getAttribute("name")).thenReturn("Nguyen Van A");

        User existingUser = User.builder().email("student@university.edu.vn").fullName("Nguyen Van A").build();
        when(userRepository.findByEmail("student@university.edu.vn")).thenReturn(Optional.of(existingUser));
        when(jwtTokenProvider.generateToken("student@university.edu.vn")).thenReturn("MOCK_JWT_TOKEN");

        oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect(contains("token=MOCK_JWT_TOKEN"));
    }
}
