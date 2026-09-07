package com.ex.learninghub.modules.audit.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.audit.entity.AuditLog;
import com.ex.learninghub.modules.audit.repository.AuditLogRepository;
import com.ex.learninghub.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void log(UserPrincipal actor, String action, String resourceType,
                    Object resourceId, String detail, String result) {
        User u = actor != null ? actor.getUser() : null;
        AuditLog log = AuditLog.builder()
                .actorId(u != null ? u.getId() : null)
                .actorEmail(u != null ? u.getEmail() : "anonymous")
                .action(action)
                .resourceType(resourceType)
                .resourceId(resourceId != null ? resourceId.toString() : null)
                .detail(detail)
                .result(result)
                .build();
        auditLogRepository.save(log);
    }
}
