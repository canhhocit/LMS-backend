package com.ex.learninghub.modules.audit.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.audit.entity.SystemErrorLog;
import com.ex.learninghub.modules.audit.repository.SystemErrorLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
@RequiredArgsConstructor
public class SystemErrorLogService {

    private static final int MAX_MESSAGE_LENGTH = 4000;
    private static final int MAX_STACK_TRACE_LENGTH = 20000;

    private final SystemErrorLogRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(Exception exception, HttpServletRequest request) {
        UserPrincipal actor = currentActor();
        StringWriter stackTraceWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTraceWriter));

        repository.save(SystemErrorLog.builder()
                .actorId(actor != null ? actor.getUser().getId() : null)
                .actorEmail(actor != null ? actor.getUser().getEmail() : "anonymous")
                .exceptionType(exception.getClass().getName())
                .errorMessage(limit(exception.getMessage(), MAX_MESSAGE_LENGTH))
                .requestMethod(request != null ? request.getMethod() : null)
                .requestPath(request != null ? limit(request.getRequestURI(), 2048) : null)
                .stackTrace(limit(stackTraceWriter.toString(), MAX_STACK_TRACE_LENGTH))
                .build());
    }

    private UserPrincipal currentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }
        return principal;
    }

    private String limit(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
