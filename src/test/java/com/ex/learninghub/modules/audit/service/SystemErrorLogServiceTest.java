package com.ex.learninghub.modules.audit.service;

import com.ex.learninghub.modules.audit.entity.SystemErrorLog;
import com.ex.learninghub.modules.audit.repository.SystemErrorLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemErrorLogServiceTest {

    @Mock
    private SystemErrorLogRepository repository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private SystemErrorLogService service;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void recordsExceptionAndRequestDetails() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/v1/admin/users");

        service.record(new IllegalStateException("Creation failed"), request);

        ArgumentCaptor<SystemErrorLog> captor = ArgumentCaptor.forClass(SystemErrorLog.class);
        verify(repository).save(captor.capture());
        SystemErrorLog saved = captor.getValue();
        assertEquals("java.lang.IllegalStateException", saved.getExceptionType());
        assertEquals("Creation failed", saved.getErrorMessage());
        assertEquals("POST", saved.getRequestMethod());
        assertEquals("/api/v1/admin/users", saved.getRequestPath());
        assertEquals("anonymous", saved.getActorEmail());
        assertTrue(saved.getStackTrace().contains("Creation failed"));
    }
}
